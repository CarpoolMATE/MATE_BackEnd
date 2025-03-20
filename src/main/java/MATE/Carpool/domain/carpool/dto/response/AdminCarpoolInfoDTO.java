package MATE.Carpool.domain.carpool.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class AdminCarpoolInfoDTO {

    private CarpoolResponseDTO carpoolInfo;
    private List<PassengerInfoDTO> passengerInfo;
    private boolean isReport;

    public AdminCarpoolInfoDTO(CarpoolResponseDTO carpoolInfo, List<PassengerInfoDTO> passengerInfo, boolean  isReport) {
        this.carpoolInfo = carpoolInfo;
        this.passengerInfo = passengerInfo;
        this.isReport = isReport;
    }
}
