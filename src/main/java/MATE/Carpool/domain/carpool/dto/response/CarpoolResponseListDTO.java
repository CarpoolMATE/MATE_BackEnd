package MATE.Carpool.domain.carpool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// 카풀 목록 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarpoolResponseListDTO {

    private String driverImg;

    private String departureCoordinate;

    private LocalDateTime departureDateTime;

    private int capacity;

    private int reservationCount;

    private int cost;

}
