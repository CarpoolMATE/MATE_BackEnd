package MATE.Carpool.domain.admin.dto;


import MATE.Carpool.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberResponseDTO {


    private Long id;
    private String memberId;
    private Boolean isBanned;
    private String nickname;
    private Long carpoolCount;
    private String carNumber;
    private String phoneNumber;

    public MemberResponseDTO(Member member){
        this.id = member.getId();
        this.memberId = member.getMemberId();
        this.isBanned = member.getIsBanned();
        this.nickname = member.getNickname();
        this.carpoolCount = member.getCarpoolCount();
        this.carNumber = member.getCarNumber();
        this.phoneNumber = member.getPhoneNumber();

    }




}
