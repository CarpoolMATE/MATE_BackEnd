package MATE.Carpool.domain.carpool.dto.response;

import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarpoolHistoryResponseDTO {

    private Long carpoolId;

    private String driverImg;

    private String departureCoordinate;

    private LocalDateTime departureTime;

    private int capacity;

    private int cost;

    private int reservationCount;

    private LocalDateTime createAt;

    public CarpoolHistoryResponseDTO(CarpoolEntity carpool) {
        this.carpoolId = carpool.getId();
        this.driverImg = carpool.getMember().getProfileImage();
        this.departureCoordinate = carpool.getDepartureCoordinate();
        this.departureTime = carpool.getDepartureDateTime();
        this.capacity = carpool.getCapacity();
        this.cost = carpool.getCost();
        this.reservationCount = carpool.getReservationCount();
        this.createAt = carpool.getCreatedAt();
    }

}
