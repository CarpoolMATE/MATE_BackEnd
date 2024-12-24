package MATE.Carpool.domain.carpool.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

//카풀 생성 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
