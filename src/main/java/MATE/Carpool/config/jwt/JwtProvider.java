package MATE.Carpool.config.jwt;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.config.userDetails.CustomUserDetailsServiceImpl;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final EntityManager entityManager;

    private Key key;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.time}")
    private int accessTimeMillis;

    @Value("${jwt.refresh.time}")
    private int refreshTimeMillis;

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
        Date accessTime = getTokenExpiration(accessTimeMillis);
        Date refreshTime = getTokenExpiration(refreshTimeMillis);
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

    public String createNewRefreshToken(Authentication authentication, long time) {
        Date date = new Date();
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(authentication.getName())
                        .setExpiration(new Date(date.getTime() + time))
                        .setIssuedAt(date)
                        .signWith(key)
                        .compact();
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

    public void accessTokenSetHeader(String accessToken, HttpServletResponse response) {
        String headerValue = BEARER_PREFIX + accessToken;
        response.setHeader(AUTHORIZATION_HEADER, headerValue);
    }

    public void refreshTokenSetHeader(String refreshToken, HttpServletResponse response) {
        response.setHeader(REFRESH_HEADER, refreshToken);
    }

    // Request Header에 Access Token 정보를 추출하는 메서드
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Request Header에 Refresh Token 정보를 추출하는 메서드
    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(REFRESH_HEADER);
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
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

    private Date getTokenExpiration(long expirationMillisecond) {
        Date date = new Date();
        return new Date(date.getTime() + expirationMillisecond);
    }

    public int getExpirationTime(String token) {
        Date expirationDate = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getExpiration();
        Date now = new Date();
        return (int)(expirationDate.getTime() - now.getTime()); // 남은 시간(밀리초 단위)
    }



    @Transactional
    public void createTokenAndSavedRefresh(Authentication authentication, HttpServletResponse response, String memberId) {
        try {
            // JWT 토큰 생성
            JwtTokenDto token = createAllToken(authentication);
            accessTokenSetHeader(token.getAccessToken(), response);
            refreshTokenSetHeader(token.getRefreshToken(), response);

            // 기존 RefreshToken 삭제
            Optional<RefreshToken> existingToken = refreshTokenRepository.findByMemberId(memberId);
            if (existingToken.isPresent()) {
                // 기존 토큰 삭제
                refreshTokenRepository.delete(existingToken.get());
                // 바로 데이터베이스에 반영되도록 flush
                entityManager.flush();
            }

            // 새로운 RefreshToken 저장
            RefreshToken refreshToken = RefreshToken.builder()
                    .refreshToken(token.getRefreshToken())
                    .memberId(memberId)
                    .expiresAt(refreshTimeMillis)
                    .build();

            // 리프레시 토큰 저장
            refreshTokenRepository.save(refreshToken);

        } catch (Exception e) {
            // 예외 처리
            log.error("Error while creating or saving refresh token for memberId: " + memberId, e);
            throw new RuntimeException("리프레시 토큰 처리 중 오류가 발생했습니다.");
        }
    }
    public void createTokenAndSavedRefreshHttponly(Authentication authentication, HttpServletResponse response, String memberId) {
        JwtTokenDto token = createAllToken(authentication);
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(accessTimeMillis/1000);

        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(refreshTimeMillis/1000);

        // 쿠키를 응답에 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        RefreshToken rToken = RefreshToken.builder()
                .refreshToken(token.getRefreshToken())
                .memberId(memberId)
                .expiresAt(refreshTimeMillis)
                .build();
        refreshTokenRepository.save(rToken);
    }







}
