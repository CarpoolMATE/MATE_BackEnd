package MATE.Carpool.domain.carpool.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CarpoolRequestDTO {

    //Member IDENTITY 값
    private String memberId;

    //탑승 좌표
    private String departureCoordinate;

    //상세 주소
    private String departureDetailed;

    private LocalDateTime departureTime;

    private String chatLink;

    private int capacity;

    private int cost;

}
