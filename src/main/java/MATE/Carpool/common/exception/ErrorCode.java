package MATE.Carpool.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT-001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "ACCOUNT-002", "존재하는 이메일입니다."),
    DUPLICATE_MEMBER_ID(HttpStatus.BAD_REQUEST, "ACCOUNT-002", "존재하는 아이디 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "ACCOUNT-003", "사용자 정보가가 일치하지 않습니다."),






    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "AUTH-403", "접근권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-002", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "지원되지 않는 토큰입니다."),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "잘못된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-005", "토큰이 존재하지 않습니다."),
    ;


    private final HttpStatus httpStatus;	// HttpStatus
    private final String code;				// ACCOUNT-001
    private final String message;			// 설명


}