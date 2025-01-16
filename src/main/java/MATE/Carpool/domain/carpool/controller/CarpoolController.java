package MATE.Carpool.domain.carpool.controller;

import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.request.ReservationCarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolHistoryResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.PassengerInfoDTO;
import MATE.Carpool.domain.carpool.service.CarpoolService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carpool")
@RequiredArgsConstructor
@Slf4j
public class CarpoolController {

    private final CarpoolService carpoolService;

    @GetMapping("/list")
    @Operation(summary = "카풀 목록", description = "홈 화면 카풀 목록 리스트를 요청합니다.")
    public ResponseEntity<List<CarpoolResponseDTO>> carpoolList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info(userDetails.getMember().getNickname());
        return carpoolService.getCarpoolList(userDetails);
    }


    @GetMapping("/{id}")
    @Operation(summary = "카풀 상세조회", description = "카풀을 입력받은 id값으로 상세조회합니다.")
    public ResponseEntity<CarpoolResponseDTO> readOne(@PathVariable("id")Long id) {
        return carpoolService.readOne(id);
    }

    @GetMapping("/active")
    @Operation(summary = "카풀 모집중 필터", description = "모집중인 카풀 목록 리스트를 요청합니다.")
    public ResponseEntity<List<CarpoolResponseDTO>> onlyActiveCarpoolList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.onlyActiveCarpoolList(userDetails);
    }

    @GetMapping("/fast")
    @Operation(summary = "카풀 시간 필터", description = "빠른 출발 시간의 카풀 리스트를 요청합니다.")
    public ResponseEntity<List<CarpoolResponseDTO>> fastCarpoolList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.fastCarpoolList(userDetails);
    }

    @GetMapping("/low")
    @Operation(summary = "카풀 가격 필터", description = "가장 낮은 가격의 카풀 리스트를 요청합니다.")
    public ResponseEntity<List<CarpoolResponseDTO>> lowCostCarpoolList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.lowCostCarpoolList(userDetails);
    }

    @GetMapping("/myCarpool")
    @Operation(summary = "내 카풀", description = "진행중인 카풀 정보를 요청합니다.")
    public ResponseEntity<List<PassengerInfoDTO>> myCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.myCarpool(userDetails);
    }

    @PostMapping("")
    @Operation(summary = "카풀 생성", description = "드라이버가 카풀 생성을 합니다.")
    public ResponseEntity<CarpoolResponseDTO> makeCarpool(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CarpoolRequestDTO carpoolRequestDTO) {
        return carpoolService.makeCarpool(userDetails, carpoolRequestDTO);
    }

    @PostMapping("/reservation")
    @Operation(summary = "카풀 예약", description = "카풀 예약을 합니다.")
    public ResponseEntity<List<PassengerInfoDTO>> reservationCarpool(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ReservationCarpoolRequestDTO requestDTO) {
        return carpoolService.reservationCarpool(userDetails, requestDTO);
    }

    @GetMapping("/cancelCarpool")
    @Operation(summary = "카풀 취소", description = "승객이 예약한 자신의 카풀을 취소합니다")
    public ResponseEntity<String> cancelCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.cancelCarpool(userDetails);
    }

    @GetMapping("/deleteCarpool")
    @Operation(summary = "카풀 삭제", description = "드라이버가 생성한 자신의 카풀을 삭제합니다.")
    public ResponseEntity<String> deleteCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.deleteCarpool(userDetails);
    }

    @GetMapping("/history")
    @Operation(summary = "카풀 이용 목록", description = "사용자의 카풀 이용 목록을 요청합니다.")
    public ResponseEntity<List<CarpoolHistoryResponseDTO>> getCarpoolHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.getCarpoolHistory(userDetails);
    }

    @GetMapping("/driveHis")
    @Operation(summary = "카풀 이용 목록", description = "사용자의 카풀 이용 목록을 요청합니다.")
    public ResponseEntity<List<CarpoolHistoryResponseDTO>> getDriverHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.getDriverHistory(userDetails);
    }

}
