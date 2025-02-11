package MATE.Carpool.domain.member.dto.response;

import MATE.Carpool.domain.member.dto.MemberDto;
import MATE.Carpool.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MemberResponseDto extends MemberDto {

    private Boolean isDriver;
    private Boolean isBanned;
    private Long carpoolCount;

    public MemberResponseDto(Member member) {
        super(member);
        this.isDriver=member.getIsDriver();
        this.isBanned=member.getIsBanned();
        this.carpoolCount=member.getCarpoolCount();

    }



}
