package MATE.Carpool.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 커스텀 에러 메시지 처리
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 Forbidden 상태 코드
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Custom error message (예: ErrorCode.FORBIDDEN_ACCESS에 맞는 메시지 반환)
        String errorMessage = "{\"status\": 403, \"name\" : \"FORBIDDEN_ACCESS\", \"code\": \"ACCOUNT-403\", \"message\": \"접근이 권한이 없습니다.\"}";

        // 응답으로 전달
        response.getWriter().write(errorMessage);
    }
}