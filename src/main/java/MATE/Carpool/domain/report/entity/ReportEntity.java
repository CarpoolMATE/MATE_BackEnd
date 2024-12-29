package MATE.Carpool.domain.report.entity;


import MATE.Carpool.common.TimeStamped;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity extends TimeStamped {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 250)
    private String reportTitle;

    @Column(nullable = false,length = 250)
    private String reportContent;

    @ManyToOne
    private Member member;

    @ManyToOne
    private CarpoolEntity carpool;




}
