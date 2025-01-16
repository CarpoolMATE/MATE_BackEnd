package MATE.Carpool.domain.report.dto;


import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.report.entity.ReportEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {

    private Long reportId;
    private Long memberId;
    private Long carpoolId;
    private String reportTitle;
    private String reportContent;

    public ReportResponseDto(ReportEntity report) {
        this.reportId = report.getId();
        this.memberId = report.getMember().getId();
        this.reportTitle = report.getReportTitle();
        this.reportContent = report.getReportContent();
        this.carpoolId = report.getCarpool().getId();

    }


}
