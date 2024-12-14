package MATE.Carpool.domain.carpool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// 예약 진행 혹은 생성한 카풀 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyCarpoolResponseDTO {

    private String carpoolId;

    private String driverName;

    private String driverImg;

    private String carImg;

    private String carNumber;

    private List<PassengerInfo> passengers;

    private String departureCoordinate;

    private String departureDetail;

    private LocalDateTime departureTime;

    private String chatLink;

    private int capacity;

    private int cost;

    private int reservationCount;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PassengerInfo {
        private String name;
        private String profileImage;
    }
}