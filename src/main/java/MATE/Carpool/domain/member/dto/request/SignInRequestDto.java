package MATE.Carpool.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {

    @Schema(description = "사용자의 아이디", example = "member")
    private String memberId;
    @Schema(description = "사용자 패스워드", example = "password123")
    private String password;
}
