package MATE.Carpool.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class GetMemberInfo {
    public record DuplicateEmail(
            @NotBlank
            @Email
            @Schema(description = "중복검사 이메일", example = "member@test.com")
            String email
    ){

    }
    public record DuplicateMemberId(
            @NotBlank
            @Schema(description = "중복검사 아이디", example = "account")
            String memberId
    ){

    }

    public record DuplicateNickname(
            @NotBlank
            @Schema(description = "중복검사 닉네임", example = "닉네임입니다")
            String nickname
    ){

    }

    public record ForgotPassword(
            @NotBlank
            String email,
            @NotBlank
            String memberId
    ){

    }

    public record Password(
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{3,20}$", message = "비밀번호는 3~20자 영문과 숫자를 포함해야 합니다.")
            @NotBlank
            String password
    ){

    }

}
