package MATE.Carpool.domain.member.dto.response;

import MATE.Carpool.domain.member.dto.MemberDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.entity.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class MemberResponseDto extends MemberDto {

    private String id;
    private ProviderType providerType = ProviderType.MATE;
    private MemberType memberType =MemberType.STANDARD;
    private Boolean isDriver;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private LocalDateTime deleteDate;

    public MemberResponseDto(String encryptId,Member member) {
        super(member);
        this.id=encryptId;
        this.providerType=member.getProviderType();
        this.memberType=member.getMemberType();
        this.isDriver=member.getIsDriver();
        this.createDate =member.getCreatedAt();
        this.updateDate =member.getUpdatedAt();
        this.deleteDate =member.getDeletedAt();

    }
    public MemberResponseDto(Member member) {
        super(member);
        this.id=member.getMemberId();
        this.providerType=member.getProviderType();
        this.memberType=member.getMemberType();
        this.isDriver=member.getIsDriver();
        this.createDate =member.getCreatedAt();
        this.updateDate =member.getUpdatedAt();
        this.deleteDate =member.getDeletedAt();
    }


}
