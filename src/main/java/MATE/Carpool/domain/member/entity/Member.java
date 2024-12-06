package MATE.Carpool.domain.member.entity;


import MATE.Carpool.common.TimeStamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column
    String memberId;

    @Column(nullable = false, unique = true)
    String nickname;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    ProviderType providerType = ProviderType.MATE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    MemberType memberType =MemberType.STANDARD;

    @Column(nullable = false)
    @Setter
    String profileImage;

    @Column
    @Setter
    Boolean reservation = false;

    @Column
    @Setter
    Boolean isDriver = false;


}

