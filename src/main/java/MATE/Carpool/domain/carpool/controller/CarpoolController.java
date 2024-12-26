package MATE.Carpool.domain.carpool.controller;

import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.request.ReservationCarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolHistoryResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.PassengerInfoDTO;
import MATE.Carpool.domain.carpool.service.CarpoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member/")
@RequiredArgsConstructor
public class CarpoolController {

    private final CarpoolService carpoolService;

    @GetMapping("list")
    public ResponseEntity<List<CarpoolResponseDTO>> carpoolList() {
        return carpoolService.GetCarpoolList();
    }

    @GetMapping("mycarpool")
    public ResponseEntity<List<PassengerInfoDTO>> myCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.myCarpool(userDetails);
    }

    @PostMapping("carpool")
    //AuthenticationPrincipal
    public ResponseEntity<List<PassengerInfoDTO>> makeCarpool(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CarpoolRequestDTO carpoolRequestDTO) {
        return carpoolService.makeCarpool(userDetails, carpoolRequestDTO);
    }

    @PostMapping("reservation")
    public ResponseEntity<List<PassengerInfoDTO>> reservationCarpool(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ReservationCarpoolRequestDTO requestDTO) {
        return carpoolService.reservationCarpool(userDetails, requestDTO);
    }

    @PostMapping("cancelCarpool")
    public ResponseEntity<String> cancelCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.cancelCarpool(userDetails);
    }

    @PostMapping("deleteCarpool")
    public ResponseEntity<String> deleteCarpool(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.deleteCarpool(userDetails);
    }

    @GetMapping("history")
    public ResponseEntity<List<CarpoolHistoryResponseDTO>> getCarpoolHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return carpoolService.getCarpoolHistory(userDetails);
    }
}
