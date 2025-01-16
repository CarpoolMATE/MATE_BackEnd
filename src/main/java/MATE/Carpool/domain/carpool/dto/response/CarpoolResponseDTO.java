package MATE.Carpool.domain.carpool.dto.response;

import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import lombok.*;

import java.time.LocalDateTime;

// 예약 진행 혹은 생성한 카풀 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CarpoolResponseDTO {

    private Long carpoolId;

    private String driverName;

    private String driverImg;

    private String carImg;

    private String carNumber;

    private String departureCoordinate;

    private LocalDateTime departureTime;

    private String chatLink;

    private int capacity;

    private int cost;

    private int reservationCount;

    public CarpoolResponseDTO(CarpoolEntity carpool){
        this.carpoolId = carpool.getId();
        this.driverName =carpool.getMember().getNickname();
        this.driverImg = carpool.getMember().getProfileImage();
        this.carImg = carpool.getMember().getCarImage();
        this.carNumber = carpool.getMember().getCarNumber();
        this.departureCoordinate = carpool.getDepartureCoordinate();
        this.departureTime =carpool.getDepartureDateTime();
        this.chatLink = carpool.getChatLink();
        this.capacity =carpool.getCapacity();
        this.cost =carpool.getCost();
        this.reservationCount =carpool.getReservationCount();
    }

}





