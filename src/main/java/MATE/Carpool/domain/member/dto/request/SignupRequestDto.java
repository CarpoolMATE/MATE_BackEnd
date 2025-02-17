package MATE.Carpool.domain.member.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    @Size(min = 3, max = 20, message = "아이디는 3자 이상 20자 이하로 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 3~20자 영문 or 숫자만 사용할 수 있습니다.")
    @Schema(description = "사용자의 아이디", example = "member")
    private String memberId;

    @Email
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "올바른 형식의 이메일을 입력해주세요.")
    @Schema(description = "사용자 이메일", example="member@test.com")
    private String email;

    @Size(min = 3, max = 20, message = "비밀번호는 3자 이상 20자 이하로 가능합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{3,20}$", message = "비밀번호는 3~20자 영문과 숫자를 포함해야 합니다.")
    @Schema(description = "사용자 패스워드", example = "password123")
    private String password;

    @Schema(description = "사용자 닉네임", example = "닉네임")
    private String nickname;

    @Schema(description = "대학교", example = "서울대학교")
    private String university;

    @Override
    public String toString() {
        return "SignInRequestDto{" +
                "memberId='" + memberId + '\'' +
                "email='" + email + '\'' +
                "nickname='" + nickname + '\'' +
                "university='" + university + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}

