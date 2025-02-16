package MATE.Carpool.config.jwt;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.redis.RedisService;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.config.userDetails.CustomUserDetailsServiceImpl;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static MATE.Carpool.common.exception.ErrorCode.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh";
    public static final String BEARER_PREFIX = "Bearer ";

    private final CustomUserDetailsServiceImpl userDetailsService;
    private final EntityManager entityManager;
    private final RedisService redisService;
    private final MemberRepository memberRepository;


    private Key key;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.time}")
    private int accessTimeSeconds;

    @Value("${jwt.refresh.time}")
    private int refreshTimeSeconds;

    @PostConstruct
    public void init() {
        String base64EncodedSecretKey = encodeBase64SecretKey(this.secretKey);
        this.key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
    }

    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtTokenDto createAllToken(Authentication authentication) {
        return new JwtTokenDto(createJwtToken(authentication, "Access"),
                createJwtToken(authentication, "Refresh"));
    }


    public String createJwtToken(Authentication authentication,String type) {
        Date accessTime = getTokenExpiration(accessTimeSeconds);
        Date refreshTime = getTokenExpiration(refreshTimeSeconds);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String authority = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if(type.equals("Access")){
           return Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("role",authority)
                    .claim("nickname",customUserDetails.getMember().getNickname())
                    .setExpiration(accessTime)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }else{
           return  Jwts.builder()
                    .setSubject(authentication.getName())
                    .setExpiration(refreshTime)
                    .signWith(key)
                    .compact();
        }
    }


    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        String role = claims.get("role", String.class);
        if (role == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(role.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        String memberId = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    public String getMemberInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }



    // 토큰 검증

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | SecurityException | MalformedJwtException e) {
            throw new CustomException(INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(WRONG_TOKEN);
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date getTokenExpiration(long expirationSecond) {
        Date date = new Date();
        return new Date(date.getTime() + expirationSecond);
    }

    public void handleRefreshToken(String refreshToken, HttpServletResponse response,HttpServletRequest request) {
        String memberId = getMemberInfoFromToken(refreshToken);
        UserIpTokenDto userIpTokenDto = redisService.getRefreshToken(memberId);
        log.info("ip: {}" ,userIpTokenDto.getIp());

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }
        } else {
            clientIp = clientIp.split(",")[0].trim();
        }
        if (!clientIp.equals(userIpTokenDto.getIp())) {
            redisService.deleteRefreshToken(memberId);
            throw new CustomException(ErrorCode.IP_MISMATCH);
        }
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        CustomUserDetails userDetails = new CustomUserDetails(member);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


        JwtTokenDto tokenDto = createAllToken(authentication);

        redisService.saveRefreshToken(memberId, tokenDto.getRefreshToken(), clientIp, refreshTimeSeconds);
        deleteCookie(response,"REFRESH_TOKEN");

        addTokenToCookies(response, tokenDto.getAccessToken(), tokenDto.getRefreshToken());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public void createTokenAndSavedTokenHttponly(Authentication authentication, HttpServletResponse response,HttpServletRequest request, String memberId) {
        JwtTokenDto token = createAllToken(authentication);
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        addTokenToCookies(response,accessToken,refreshToken);

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }
        } else {
            clientIp = clientIp.split(",")[0].trim();
        }

        redisService.saveRefreshToken(memberId,refreshToken,clientIp,refreshTimeSeconds);
    }

    public void addTokenToCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(accessTimeSeconds/1000);
//        accessTokenCookie.setDomain(".carpool.com");

        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(refreshTimeSeconds/1000);
//        refreshTokenCookie.setDomain(".carpool.com");

        // 쿠키를 응답에 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

    }

    public void setAuthentication(String access_token) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = getAuthentication(access_token);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    public void deleteCookie(HttpServletResponse response, String cookieName) {
        // 만료 날짜를 과거로 설정하여 쿠키를 삭제
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }




}
