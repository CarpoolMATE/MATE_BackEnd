package MATE.Carpool.config.jwt;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.redis.RedisService;
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

import static MATE.Carpool.config.jwt.JwtProvider.ACCESS_KEY;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String accessToken = getCookieValue(request, "ACCESS_TOKEN");
//        String refreshToken = getCookieValue(request, "REFRESH_TOKEN");
//
//        if(accessToken != null && jwtProvider.validateToken(accessToken)) {
//            jwtProvider.setAuthentication(accessToken);
//        }else if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
//            jwtProvider.handleRefreshToken(refreshToken, response,request);
//        }
//
////        log.info("Authentication method: {}",  "Cookie");
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String getCookieValue(HttpServletRequest request, String cookieName) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookieName.equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String access_token = jwtProvider.resolveToken(request, ACCESS_KEY);

    if(access_token != null){
        if(jwtProvider.validateToken(access_token)){
            jwtProvider.setAuthentication(access_token);
        }
    }

    filterChain.doFilter(request, response);
}










}
