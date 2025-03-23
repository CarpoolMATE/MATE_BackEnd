package MATE.Carpool.domain.report.dto;


import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.report.entity.ReportEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {

    private Long reportId;
    private Long memberId;
    private String memberNickname;
    private LocalDateTime createAt;
    private Long carpoolId;
    private String reportTitle;
    private String reportContent;
    private boolean isProcessed;


    public ReportResponseDto(ReportEntity report) {
        this.reportId = report.getId();
        this.memberId = report.getMember().getId();
        this.memberNickname = report.getMember().getNickname();
        this.reportTitle = report.getReportTitle();
        this.reportContent = report.getReportContent();
        this.carpoolId = report.getCarpool().getId();
        this.createAt = report.getCreatedAt();
        this.isProcessed=report.getIsProcessed();

    }


}
