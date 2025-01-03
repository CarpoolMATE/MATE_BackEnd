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
            String accessToken = jwtProvider.resolveAccessToken(request);
            String refreshToken = jwtProvider.resolveRefreshToken(request);

            if (accessToken != null) {

                if (jwtProvider.validateToken(accessToken)) {
                    setAuthentication(accessToken);
                } else if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
                    handleRefreshToken(refreshToken, response);
                } else {
                    throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
                }
            }
        filterChain.doFilter(request, response);
    }


    private void handleRefreshToken(String refreshToken, HttpServletResponse response) {
        String memberId = jwtProvider.getMemberInfoFromToken(refreshToken);
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새 Access Token 및 Refresh Token 발급
        CustomUserDetails userDetails = new CustomUserDetails(member);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = jwtProvider.createJwtToken(authentication,"Access");

        long refreshTokenTime = jwtProvider.getExpirationTime(refreshToken);
        String newRefreshToken = jwtProvider.createNewRefreshToken(authentication, refreshTokenTime);

        RefreshToken token = refreshTokenRepository.findByMemberId(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.TOKEN_NOT_FOUND));
        token.setExpiresAt(refreshTokenTime);
        refreshTokenRepository.save(token);

        // 새 토큰을 헤더에 추가
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
