package MATE.Carpool.domain.member.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SignupRequestDto {

    private String memberId;

//    @Email
//    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "올바른 형식의 이메일을 입력해주세요.")
    private String email;

//    @Size(min = 5, max = 20, message = "비밀번호는 5자 이상 20자 이하로 가능합니다.")
//    @Pattern(regexp = "^[a-zA-Z\\p{Punct}0-9]*$", message = "비밀번호는 5~20자 영문, 숫자를 사용하세요.")
    private String password;

    private String nickname;
}
