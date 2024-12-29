package MATE.Carpool.domain.carpool.entity;

import MATE.Carpool.common.TimeStamped;
import MATE.Carpool.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReservationEntity extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CarpoolEntity carpool;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
