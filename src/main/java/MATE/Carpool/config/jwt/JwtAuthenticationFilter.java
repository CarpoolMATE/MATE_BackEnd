package MATE.Carpool.config.jwt;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        //swagger 처리용
        if (request.getHeader("Referer") !=null && request.getHeader("Referer").contains("swagger")) {

            String accessTokenHeader = jwtProvider.resolveAccessToken(request);
            String refreshTokenHeader = jwtProvider.resolveRefreshToken(request);

            if(accessTokenHeader != null &&jwtProvider.validateToken(accessTokenHeader)) {
                setAuthentication(accessTokenHeader);
            }else if(refreshTokenHeader != null &&jwtProvider.validateToken(refreshTokenHeader)) {
                handleRefreshToken(refreshTokenHeader, response);
            }
            log.info("Authentication method: {}",  "Header(Swagger)");
            filterChain.doFilter(request, response);
            return;
        }
        
        //일반 처리
        String accessToken = getCookieValue(request, "ACCESS_TOKEN");
        String refreshToken = getCookieValue(request, "REFRESH_TOKEN");

        if(accessToken != null && jwtProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        }else if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
            handleRefreshToken(refreshToken, response);
        }
        log.info("Authentication method: {}",  "Cookie");

        filterChain.doFilter(request, response);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void handleRefreshToken(String refreshToken, HttpServletResponse response) {
        String memberId = jwtProvider.getMemberInfoFromToken(refreshToken);
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        CustomUserDetails userDetails = new CustomUserDetails(member);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = jwtProvider.createJwtToken(authentication,"Access");

        int refreshTokenTime = jwtProvider.getExpirationTime(refreshToken);
        String newRefreshToken = jwtProvider.createNewRefreshToken(authentication, refreshTokenTime);

        RefreshToken token = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.TOKEN_NOT_FOUND));
        token.setExpiresAt(refreshTokenTime);
        refreshTokenRepository.save(token);

        jwtProvider.accessTokenSetHeader(newAccessToken, response);
        jwtProvider.refreshTokenSetHeader(newRefreshToken, response);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }



    public void setAuthentication(String access_token) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtProvider.getAuthentication(access_token);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }




}
