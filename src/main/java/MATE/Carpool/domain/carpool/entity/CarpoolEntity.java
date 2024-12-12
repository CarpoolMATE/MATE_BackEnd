package MATE.Carpool.domain.carpool.entity;

import MATE.Carpool.common.TimeStamped;
import MATE.Carpool.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CarpoolEntity extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @Column
    private String departureCoordinate;

    @Column
    private String departureDetailed;

    @Column
    private LocalDateTime departureTime;

    @Column
    private String chatLink;

    @Column
    private int capacity;

    @Column
    private int cost;
}