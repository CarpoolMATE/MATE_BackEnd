package MATE.Carpool.domain.carpool.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

//카풀 생성 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CarpoolRequestDTO {

    @Schema(description = "주소 입니다.", example="수원시 권선구 오목천동")
    @NotBlank
    private String departureCoordinate;

    @Schema(description = "위도 입니다.", example="37.566826")
    @NotBlank
    private String latitude;

    @Schema(description = "경도 입니다.", example="126.9786567")
    @NotBlank
    private String longitude;

    @Schema(description = "출발 날짜,시간 입니다.", example="2025-02-23T08:00:00")
    @NotNull(message = "출발 날짜와 시간을 입력하세요.")
    private LocalDateTime departureTime;

    @Schema(description = "카카오 오픈채팅방 링크 입니다.", example="kakao.com")
    @NotBlank
    private String chatLink;

    @Schema(description = "최대탑승 인원 수 입니다.", example="4")
    @NotNull(message = "최대 탑승 인원을 입력하세요.")
    private int capacity;

    @Schema(description = "요금 입니다.", example="2000")
    @NotNull(message = "요금을 입력하세요.")
    private int cost;

}
