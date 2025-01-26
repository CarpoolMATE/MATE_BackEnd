package MATE.Carpool.domain.member.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FindPasswordRequestDto {

    private String memberId;
    private String email;

}
