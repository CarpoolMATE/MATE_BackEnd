package MATE.Carpool.common.exception;


import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e) {
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }




    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponseEntity> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseEntity(400, "BAE REQUEST","ACCOUNT-004", "사용자 정보가 일치하지않습니다"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponseEntity> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseEntity(400, "BAD_REQUEST","","잘못된 요청입니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponseEntity> handleAccessDeniedException(AccessDeniedException e) {
        // 권한 부족 예외에 대한 커스텀 응답
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseEntity(403, "FORBIDDEN_ACCESS", "", "접근이 거부되었습니다."));
    }

//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<ErrorResponseEntity> handleGlobalException(Exception e) {
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ErrorResponseEntity(500,"INTERNAL_SERVER_ERROR", "알 수 없는 오류가 발생했습니다.",""));
//    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponseEntity> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 유효성 검사 오류 메시지 모으기
//        List<String> errorMessages = ex.getBindingResult()
//                .getAllErrors()
//                .stream()
//                .map(ObjectError::getDefaultMessage)
//                .toList();
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        // 오류 메시지를 포함한 응답 생성
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseEntity(400, "BAD_REQUEST", "입력값 오류", message));
    }
}