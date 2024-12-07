package MATE.Carpool.domain.member.dto.response;

import MATE.Carpool.domain.member.dto.MemberDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.entity.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MemberResponseDto extends MemberDto {

    private String id;
    private ProviderType providerType = ProviderType.MATE;
    private MemberType memberType =MemberType.STANDARD;
    private Boolean isDriver;

    public MemberResponseDto(String encryptId,Member member) {
        super(member);
        this.id=encryptId;
        this.providerType=member.getProviderType();
        this.memberType=member.getMemberType();
        this.isDriver=member.getIsDriver();

    }


}
