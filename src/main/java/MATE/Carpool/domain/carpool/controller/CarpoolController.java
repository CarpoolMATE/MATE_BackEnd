package MATE.Carpool.domain.carpool.controller;

import MATE.Carpool.common.Message;
import MATE.Carpool.common.swagger.CarpoolApi;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.request.ReservationCarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolHistoryResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.PassengerInfoDTO;
import MATE.Carpool.domain.carpool.service.CarpoolService;
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
public class CarpoolController implements CarpoolApi {

    private final CarpoolService carpoolService;

    @GetMapping("/list")
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> carpoolList() {
        return carpoolService.getCarpoolList();
    }

    @GetMapping("/active")
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> onlyActiveCarpoolList() {
        return carpoolService.onlyActiveCarpoolList();
    }

    @GetMapping("/fast")
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> fastCarpoolList() {
        return carpoolService.fastCarpoolList();
    }

    @GetMapping("/low")
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> lowCostCarpoolList() {
        return carpoolService.lowCostCarpoolList();
    }

    @GetMapping("/myCarpool")
        public ResponseEntity<Message<List<PassengerInfoDTO>>> myCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.myCarpool(userDetails);
    }

    @PostMapping("")
    public ResponseEntity<Message<CarpoolResponseDTO>> makeCarpool(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CarpoolRequestDTO carpoolRequestDTO) {
        return carpoolService.makeCarpool(userDetails, carpoolRequestDTO);
    }

    @PostMapping("/reservation")
    public ResponseEntity<Message<List<PassengerInfoDTO>>> reservationCarpool(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ReservationCarpoolRequestDTO requestDTO) {
        return carpoolService.reservationCarpool(userDetails, requestDTO);
    }

    @GetMapping("/cancelCarpool")
    public ResponseEntity<Message<String>> cancelCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.cancelCarpool(userDetails);
    }

    @GetMapping("/deleteCarpool")
    public ResponseEntity<Message<String>> deleteCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.deleteCarpool(userDetails);
    }

    @GetMapping("/history")
    public ResponseEntity<Message<List<CarpoolHistoryResponseDTO>>> getCarpoolHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.getCarpoolHistory(userDetails);
    }

    @GetMapping("/driveHis")
    public ResponseEntity<Message<List<CarpoolHistoryResponseDTO>>> getDriverHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.getDriverHistory(userDetails);
    }

}
