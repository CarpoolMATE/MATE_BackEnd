package MATE.Carpool.config.jwt;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.common.exception.ErrorResponseEntity;
import MATE.Carpool.config.redis.RedisService;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.config.userDetails.CustomUserDetailsServiceImpl;
import MATE.Carpool.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
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


    //삭제
    public static final String ACCESS_KEY ="Authorization";
    public static final String REFRESH_KEY ="RefreshToken";


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


//    public String createJwtToken(Authentication authentication,String type) {
//        Date accessTime = getTokenExpiration(accessTimeSeconds);
//        Date refreshTime = getTokenExpiration(refreshTimeSeconds);
//        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//        String authority = authentication.getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//        if(type.equals("Access")){
//           return Jwts.builder()
//                    .setSubject(authentication.getName())
//                    .claim("role",authority)
//                    .claim("nickname",customUserDetails.getMember().getNickname())
//                    .setExpiration(accessTime)
//                    .signWith(key, SignatureAlgorithm.HS256)
//                    .compact();
//        }else{
//           return  Jwts.builder()
//                    .setSubject(authentication.getName())
//                    .setExpiration(refreshTime)
//                    .signWith(key)
//                    .compact();
//        }
//    }


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

    public boolean validateToken(String token,boolean isRefreshToken, HttpServletResponse response) throws IOException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            if (isRefreshToken) {
                expiredRefresh(response);
            }
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("잘못된 JWT 토큰 입니다.");
        }
        return false;
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

    public void handleRefreshToken(String refreshToken, HttpServletResponse response,HttpServletRequest request) throws IOException {

        String memberId = getMemberInfoFromToken(refreshToken);
        UserIpTokenDto userIpTokenDto = redisService.getRefreshToken(memberId);

        if (checkRefreshToken(refreshToken, response, userIpTokenDto)) return;

        String clientIp = getClientIp(request, userIpTokenDto);

        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        createTokenToHandleRefresh(authentication,response,request,clientIp);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private static boolean checkRefreshToken(String refreshToken, HttpServletResponse response, UserIpTokenDto userIpTokenDto) throws IOException {
        if (!userIpTokenDto.getToken().equals(refreshToken)) {

            ErrorResponseEntity errorResponse = ErrorResponseEntity.builder()
                    .status(ErrorCode.NOT_EQUALS_REFRESH_TOKEN.getHttpStatus().value())
                    .name(ErrorCode.NOT_EQUALS_REFRESH_TOKEN.name())
                    .code(ErrorCode.NOT_EQUALS_REFRESH_TOKEN.getCode())
                    .message(ErrorCode.NOT_EQUALS_REFRESH_TOKEN.getMessage())
                    .build();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
            return true;
        }
        return false;
    }

    private static void expiredRefresh( HttpServletResponse response) throws IOException {

        ErrorResponseEntity errorResponse = ErrorResponseEntity.builder()
                .status(ErrorCode.EXPIRED_REFRESH_TOKEN.getHttpStatus().value())
                .name(ErrorCode.EXPIRED_REFRESH_TOKEN.name())
                .code(ErrorCode.EXPIRED_REFRESH_TOKEN.getCode())
                .message(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage())
                .build();

        response.setStatus(418);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();


    }

    private static String getClientIp(HttpServletRequest request, UserIpTokenDto userIpTokenDto) {
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
            throw new CustomException(ErrorCode.IP_MISMATCH);
        }
        return clientIp;
    }

//    public void createTokenAndSavedTokenHttponly(Authentication authentication, HttpServletResponse response,HttpServletRequest request, String memberId) {
//        JwtTokenDto token = createAllToken(authentication);
//        String accessToken = token.getAccessToken();
//        String refreshToken = token.getRefreshToken();
//
//        addTokenToCookies(response,accessToken,refreshToken);
//
//        String clientIp = request.getHeader("X-Forwarded-For");
//        if (clientIp == null || clientIp.isEmpty()) {
//            clientIp = request.getRemoteAddr();
//            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
//                clientIp = "127.0.0.1";
//            }
//        } else {
//            clientIp = clientIp.split(",")[0].trim();
//        }
//
//        redisService.saveRefreshToken(memberId,refreshToken,clientIp,refreshTimeSeconds);
//    }

//    public void addTokenToCookies(HttpServletResponse response, String accessToken, String refreshToken) {
//        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", accessToken);
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setSecure(false);
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setMaxAge(accessTimeSeconds/1000);
//        accessTokenCookie.setDomain("*");
//
//        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(false);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(refreshTimeSeconds/1000);
//        refreshTokenCookie.setDomain("*");
//
//        // 쿠키를 응답에 추가
//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);
//
//    }



//    public void deleteCookie(HttpServletResponse response, String cookieName) {
//        // 만료 날짜를 과거로 설정하여 쿠키를 삭제
//        Cookie cookie = new Cookie(cookieName, null);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(false);
//        cookie.setPath("/");
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//    }

    public void setAuthentication(String access_token) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = getAuthentication(access_token);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

//==============================================================

    public String createJwtToken(Authentication authentication,String type) {
        Date accessTime = getTokenExpiration(accessTimeSeconds);
        Date refreshTime = getTokenExpiration(refreshTimeSeconds);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String authority = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        if(type.equals("Access")){
            return  BEARER_PREFIX
                    + Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("role",authority)
                    .claim("nickname",customUserDetails.getMember().getNickname())
                    .setExpiration(accessTime)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }else{
            return  BEARER_PREFIX
                    + Jwts.builder()
                    .setSubject(authentication.getName())
                    .setExpiration(refreshTime)
                    .signWith(key)
                    .compact();
        }
    }

    public String resolveToken(HttpServletRequest request, String token) {
        String tokenName = token.equals("Authorization") ? ACCESS_KEY : REFRESH_KEY;
        String bearerToken = request.getHeader(tokenName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public void createTokenAndSaved(Authentication authentication, HttpServletResponse response,HttpServletRequest request) {
        JwtTokenDto token = createAllToken(authentication);
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }
        } else {
            clientIp = clientIp.split(",")[0].trim();
        }

        redisService.saveRefreshToken(authentication.getName(),refreshToken,clientIp,refreshTimeSeconds);

        setHeaderToken(response,accessToken,refreshToken);
    }

    public void createTokenToHandleRefresh(Authentication authentication, HttpServletResponse response,HttpServletRequest request,String clientIp) {
        JwtTokenDto token = createAllToken(authentication);
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        redisService.saveRefreshToken(authentication.getName(),refreshToken,clientIp,refreshTimeSeconds);

        setHeaderToken(response,accessToken,refreshToken);
    }

    public void setHeaderToken(HttpServletResponse response, String accessToken,String refreshToken) {
        response.setHeader(ACCESS_KEY, accessToken);
        response.setHeader(REFRESH_KEY, refreshToken);
    }


}
