package MATE.Carpool.domain.carpool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarpoolWithPassengersDTO {

    private CarpoolResponseDTO carpoolInfo; // 카풀 정보
    private List<PassengerInfoDTO> passengers; // 탑승자 목록
    private boolean amIDriver;

}