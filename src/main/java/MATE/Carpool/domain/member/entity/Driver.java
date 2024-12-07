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
    private Long id;

    @Column
    private String carNumber;

    @Column
    private String phoneNumber;

    @Column
    private String carImage;

    @OneToOne
    private Member member;



}
