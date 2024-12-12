package MATE.Carpool.domain.carpool.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 카풀 목록 DTO
@Getter
@Setter
public class CarpoolResponseListDTO {

    //드라이버 이미지
    private String driverImg;

    //탑승 좌표
    private String departureCoordinate;

    private LocalDateTime departureTime;

    private int capacity;

    private int reservationCount;

    private int cost;

}
