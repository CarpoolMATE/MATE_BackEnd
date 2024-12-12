package MATE.Carpool.domain.member.dto.request;


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

//    @Size(min = 3, max = 20, message = "아이디는는 3자 이상 20자 이하로 가능합니다.")
//    @Pattern(regexp = "^[a-zA-Z]*$", message = "아이디는 3~20자 영문을 사용하세요.")
    private String memberId;

//    @Email
//    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$", message = "올바른 형식의 이메일을 입력해주세요.")
    private String email;

//    @Size(min = 3, max = 20, message = "비밀번호는 3자 이상 20자 이하로 가능합니다.")
//    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{3,20}$", message = "비밀번호는 3~20자 영문과 숫자를 포함해야 합니다.")
    private String password;

    private String nickname;
}

