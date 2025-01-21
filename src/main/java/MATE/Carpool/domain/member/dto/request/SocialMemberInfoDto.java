package MATE.Carpool.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialMemberInfoDto {
    private String nickname;
    private String email;
    private String profileImage;
}
//TODO: 용도가 궁금합니다
