package MATE.Carpool.domain.member.dto;

import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.entity.ProviderType;
import jakarta.persistence.Column;
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
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private LocalDateTime deleteDate;
    private Boolean reservation;
    private Long carpoolId;

    private String carNumber;
    private String phoneNumber;
    private String carImage;
    private LocalDateTime driverRegistrationDate;
    private LocalDateTime driverCancellationDate;


    public MemberDto(Member member) {
        this.memberId = member.getMemberId();
        this.nickname = member.getNickname();
        this.email = member.getEmail();
        this.profileImage = member.getProfileImage();
        this.memberType = member.getMemberType();
        this.providerType = member.getProviderType();
        this.createDate =member.getCreatedAt();
        this.updateDate =member.getUpdatedAt();
        this.deleteDate =member.getDeletedAt();
        this.carNumber = member.getCarNumber();
        this.phoneNumber = member.getPhoneNumber();
        this.carImage = member.getCarImage();
        this.driverRegistrationDate = member.getDriverRegistrationDate();
        this.driverCancellationDate = member.getDriverCancellationDate();
        this.reservation = getReservation();
        this.carpoolId = getCarpoolId();

    }




}
