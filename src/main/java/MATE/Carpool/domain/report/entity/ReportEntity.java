package MATE.Carpool.domain.report.entity;


import MATE.Carpool.common.TimeStamped;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
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



    @Column
    private boolean isProcessed =false;

    @ManyToOne
    private Member member;

    @ManyToOne
    @JoinColumn(name = "carpool_id")
    private CarpoolEntity carpool;

    public boolean getIsProcessed() {
        return isProcessed;
    }

    public void isProcess() {
        this.isProcessed = !this.isProcessed;
    }




}
