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

    @Column
    @Setter
    @Builder.Default
    private String profileImage="basic image";

    @Column
    @Setter
    @Builder.Default
    private Boolean reservation = false;

    @Column
    @Setter
    @Builder.Default
    private Boolean isDriver = false;


}

