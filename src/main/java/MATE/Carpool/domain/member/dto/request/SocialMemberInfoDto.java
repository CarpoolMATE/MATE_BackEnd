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
    private String username;
    private String nickname;
    private String email;
    private String profileImage;
}
