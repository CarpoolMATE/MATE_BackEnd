package MATE.Carpool.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class Duplicate {
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

}
