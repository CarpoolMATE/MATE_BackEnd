package MATE.Carpool.domain.member.entity;


import MATE.Carpool.common.TimeStamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String memberId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ProviderType providerType = ProviderType.MATE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private MemberType memberType =MemberType.STANDARD;

    @Column(nullable = false)
    @Builder.Default
    private String profileImage="https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png";

    @Column
    @Builder.Default
    private Long carpoolCount = 0L;

    @Column
    private Long carpoolId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean     reservation = false;

    @Column
    @Builder.Default
    private Boolean isBanned =false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDriver = false;


    //driver
    @Column
    private String carNumber;
    @Column
    private String phoneNumber;
    @Column
    private String carImage;
    @Column
    private LocalDateTime driverRegistrationDate;
    @Column
    private LocalDateTime driverCancellationDate;

    public void incrementCarpoolCount() {
        this.carpoolCount++;
    }
    public void decrementCarpoolCount(){
        this.carpoolCount--;
    }



}

