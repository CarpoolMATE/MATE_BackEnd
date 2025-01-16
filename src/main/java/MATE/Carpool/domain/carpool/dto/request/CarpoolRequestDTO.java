package MATE.Carpool.domain.carpool.dto.request;

import lombok.*;

import java.time.LocalDateTime;

//카풀 생성 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CarpoolRequestDTO {

    //탑승 좌표
    private String departureCoordinate;

    private LocalDateTime departureTime;

    private String chatLink;

    private int capacity;

    private int cost;

}
