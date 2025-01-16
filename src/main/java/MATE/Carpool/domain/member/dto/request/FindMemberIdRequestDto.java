package MATE.Carpool.domain.member.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindMemberIdRequestDto {

    private String nickname;
    private String email;
}
