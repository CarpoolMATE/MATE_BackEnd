package MATE.Carpool.domain.member.dto.response;

import MATE.Carpool.domain.member.dto.MemberDto;
import MATE.Carpool.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class MemberResponseDto extends MemberDto {

    private Boolean isDriver;

    public MemberResponseDto(Member member) {
        super(member);
        this.isDriver=member.getIsDriver();
    }



}
