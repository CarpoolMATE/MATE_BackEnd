package MATE.Carpool.domain.member.dto.response;

import MATE.Carpool.domain.member.dto.MemberDto;
import MATE.Carpool.domain.member.entity.Driver;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.entity.ProviderType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class MemberResponseDto extends MemberDto {

    private String id;
    private Boolean isDriver;
    private DriverResponseDto driver;

    public MemberResponseDto(String encryptId,Member member) {
        super(member);
        this.id=encryptId;
        this.isDriver=member.getIsDriver();
    }
    public MemberResponseDto(Member member) {
        super(member);
        this.id=member.getMemberId();
        this.isDriver=member.getIsDriver();
    }
    public MemberResponseDto(String encryptId, Member member, Driver driver) {
        super(member);
        this.id=encryptId;
        this.isDriver=member.getIsDriver();
        this.driver=new DriverResponseDto(driver);
    }


}
