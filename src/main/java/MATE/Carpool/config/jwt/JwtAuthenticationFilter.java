package MATE.Carpool.config.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static MATE.Carpool.config.jwt.JwtProvider.ACCESS_KEY;
import static MATE.Carpool.config.jwt.JwtProvider.REFRESH_HEADER;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String access_token = jwtProvider.resolveToken(request, ACCESS_KEY);
        String refresh_token = jwtProvider.resolveToken(request,REFRESH_HEADER);

        if(access_token != null){
            if(jwtProvider.validateToken(access_token,false,response)){
                jwtProvider.setAuthentication(access_token);
            }else if(jwtProvider.validateToken(refresh_token,true, response)){
                jwtProvider.handleRefreshToken(refresh_token, response, request);
            }
        }

        filterChain.doFilter(request, response);

    }











}
