package MATE.Carpool.domain.member.dto.response;

import MATE.Carpool.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UpdateMemberResponseDto {

    private String nickname;

    private String profileImage;

    public UpdateMemberResponseDto(Member member){
        this.nickname =member.getNickname();
        this.profileImage=member.getProfileImage();

    }
}
