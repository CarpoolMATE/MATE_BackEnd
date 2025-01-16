package MATE.Carpool.domain.report.controller;


import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.report.dto.ReportRequestDto;
import MATE.Carpool.domain.report.dto.ReportResponseDto;
import MATE.Carpool.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
@Tag(name = "카풀 신고")
public class ReportController {


    private final ReportService reportService;



    @PostMapping("/{carpoolId}")
    @Operation(summary = "신고 하기", description = "유저가 이용했던 카풀을 신고합니다.")
    public ResponseEntity<String> submitReport(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ReportRequestDto requestDto,
            @PathVariable("carpoolId") Long carpoolId) {
        System.out.println(carpoolId);
       return reportService.submitReport(requestDto,carpoolId,userDetails);
    }


}
