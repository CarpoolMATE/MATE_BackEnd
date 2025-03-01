package MATE.Carpool.domain.admin.controller;

import MATE.Carpool.common.Message;
import MATE.Carpool.common.swagger.AdminApi;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.admin.dto.CarpoolResponseResultDTO;
import MATE.Carpool.domain.admin.dto.MemberResponseDTO;
import MATE.Carpool.domain.admin.dto.MemberResponseResultDTO;
import MATE.Carpool.domain.admin.service.AdminService;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.service.CarpoolService;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.service.MemberService;
import MATE.Carpool.domain.report.dto.ReportResponseDto;
import MATE.Carpool.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 관련 API")
public class AdminController implements AdminApi {

    private final AdminService adminService;
    private final MemberService memberService;



    //회원 전체조회
    @GetMapping("/members")
    public ResponseEntity<Message<MemberResponseResultDTO>> readAllMember(@RequestParam("size") int size, @RequestParam("page") int page){
        return adminService.readAllMembers(size,page);
    }

    @GetMapping("/drivers")
    public ResponseEntity<Message<MemberResponseResultDTO>> readAllDriver(@RequestParam("size") int size, @RequestParam("page") int page){
        return adminService.readAllDrivers(size,page);
    }


    @GetMapping("/member/{memberId}")
    public ResponseEntity<Message<MemberResponseDto>> readOneMember(
            @Parameter(description = "회원 ID")
            @PathVariable("memberId") Long memberId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return memberService.readOne(memberId);
    }

    //회원정지
    @Override
    @GetMapping("/member/isBanned/{memberId}")
    public ResponseEntity<Message<Boolean>> isBanned(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("memberId") Long memberId) {
        return adminService.isBanned(userDetails,memberId);
    }

    @GetMapping("/carpool/{id}")
    public ResponseEntity<Message<CarpoolResponseDTO>> readOneCarpool(@PathVariable("id")Long id) {
        return adminService.readOne(id);
    }

    @GetMapping("/carpools")
    public ResponseEntity<Message<CarpoolResponseResultDTO>> readAllCarpool(
            @RequestParam("size") int size,
            @RequestParam("page") int page,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 23:59:59


        return adminService.readAllCarpool(size, page, startDateTime, endDateTime);

    }

    @GetMapping("/carpool/report/{id}")
    @Operation(summary = "카풀 신고 조회", description = "입력받은 카풀 id에 해당하는 모든 신고목록을 조회합니다.")
    public ResponseEntity<Message<List<ReportResponseDto>>> readAllByCarpool(@PathVariable("id") Long carpoolId, int size, int page){
        return adminService.readAllByCarpool(carpoolId);
    }


    //신고내용확인
    @GetMapping("/reports")
    @Operation(summary = "신고 접수 모아보기", description = "관리자가 유저가 신고한 전체목록을 확인합니다.")
    public ResponseEntity<Message<List<ReportResponseDto>>> readAllReports(int size, int page) {
        return adminService.reportFindAll();

    }
    @GetMapping("/report/{id}")
    @Operation(summary = "신고 내용 상세보기", description = "관리자가 유저가 신고한 목록을 상세보기 합니다.")
    public ResponseEntity<Message<ReportResponseDto>> readReport(@PathVariable("id") Long reportId) {
        return adminService.reportFindById(reportId);

    }



}
