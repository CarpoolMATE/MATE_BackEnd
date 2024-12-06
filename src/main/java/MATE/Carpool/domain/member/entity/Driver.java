package MATE.Carpool.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String carNumber;

    @Column
    String phoneNumber;

    @Column
    String carImage;

    @OneToOne
    private Member member;



}
