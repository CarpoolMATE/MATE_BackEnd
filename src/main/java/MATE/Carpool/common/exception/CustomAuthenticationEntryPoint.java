package MATE.Carpool.common.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 응답 인코딩을 UTF-8로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized 상태 코드
        response.setContentType("application/json; charset=UTF-8");  // 콘텐츠 타입에 문자 인코딩 추가
        response.setCharacterEncoding("UTF-8");  // 문자 인코딩을 UTF-8로 설정

        // 한글 메시지 출력
        String errorMessage = "{\"status\": 401, \"code\": \"UNAUTHORIZED\", \"message\": \"권한이 없습니다.\"}";
        response.getWriter().write(errorMessage);
    }
}
