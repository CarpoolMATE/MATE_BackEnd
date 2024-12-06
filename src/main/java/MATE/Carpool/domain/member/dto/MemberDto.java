package MATE.Carpool.domain.member.dto;

import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.entity.ProviderType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberDto {

    private String memberId;
    private String nickname;
    private String email;
    private String profileImage;





    private ProviderType providerType = ProviderType.MATE;
    private MemberType memberType =MemberType.STANDARD;
}
