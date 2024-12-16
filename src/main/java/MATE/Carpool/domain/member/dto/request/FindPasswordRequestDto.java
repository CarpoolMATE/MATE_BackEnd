package MATE.Carpool.domain.member.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FindPasswordRequestDto {

    private String memberId;
    private String email;

}
