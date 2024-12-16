package MATE.Carpool.domain.report.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDto {

    private String reportTitle;
    private String reportContent;
}
