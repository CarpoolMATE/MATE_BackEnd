package MATE.Carpool.domain.carpool.entity;

import MATE.Carpool.common.TimeStamped;
import MATE.Carpool.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class CarpoolEntity extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @Column
    private String departureCoordinate;

    @Column //위도
    private String latitude;

    @Column //경도
    private String longitude;

    @Column
    private LocalDateTime departureDateTime;

    @Column
    private String chatLink;

    @Column
    private Integer capacity;

    @Column
    private Integer cost;

    @Column
    private Integer reservationCount = 0;


    public void incrementReservationCount(){
        this.reservationCount++;
    }
    public void decrementReservationCount(){
        this.reservationCount--;
    }
}