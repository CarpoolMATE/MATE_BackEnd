package MATE.Carpool.domain.member.entity;

import MATE.Carpool.common.TimeStamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Driver extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String carNumber;

    @Column
    private String phoneNumber;

    @Column
    private String carImage;

    @OneToOne(fetch = FetchType.LAZY)
    private Member member;



}
