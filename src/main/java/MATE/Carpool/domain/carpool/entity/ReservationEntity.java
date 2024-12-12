package MATE.Carpool.domain.carpool.entity;

import MATE.Carpool.common.TimeStamped;
import MATE.Carpool.domain.member.entity.Member;
import jakarta.persistence.*;

@Entity
public class ReservationEntity extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CarpoolEntity carpool;

    @OneToOne
    private Member member;
}
