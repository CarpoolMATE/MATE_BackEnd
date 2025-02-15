package MATE.Carpool.domain.member.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ResetPasswordResponse {

    private String message;
    private boolean valid;
}
