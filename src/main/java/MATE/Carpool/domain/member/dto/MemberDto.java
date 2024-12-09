package MATE.Carpool.domain.member.dto;

import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.entity.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class MemberDto {

    private String memberId;
    private String nickname;
    private String email;
    private String profileImage;
    private MemberType memberType;
    private ProviderType providerType;


    public MemberDto(Member member) {
        this.memberId = member.getMemberId();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.profileImage = member.getProfileImage();
        this.memberType = member.getMemberType();
        this.providerType = member.getProviderType();


    }




}
