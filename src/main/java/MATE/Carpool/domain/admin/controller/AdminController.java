package MATE.Carpool.domain.admin.controller;

import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.admin.service.AdminService;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.service.CarpoolService;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.report.dto.ReportResponseDto;
import MATE.Carpool.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "관리자 관련 API")
public class AdminController {

    private final AdminService adminService;



    //회원 전체조회
    @GetMapping()
    @Operation(summary = "회원전체 조회", description = "관리자가 가입된 모든 전체회원을 조회합니다.")
    public ResponseEntity<List<MemberResponseDto>> readAll(){
        return adminService.readAll();
    }

    //회원정지
    @GetMapping("/isBanned/{memberId}")
    @Operation(summary = "회원 정지/정지해제 처리", description = "관리자가 회원을 정지 처리 및 해제 합니다.")
    public ResponseEntity<String> isBanned(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("memberId") String memberId) {
        return adminService.isBanned(userDetails,memberId);
    }

    @GetMapping("/carpool/{id}")
    @Operation(summary = "카풀 상세조회", description = "카풀을 입력받은 id값으로 상세조회합니다.")
    public ResponseEntity<CarpoolResponseDTO> readOne(@PathVariable("id")Long id) {
        return adminService.readOne(id);
    }

    @GetMapping("/carpools")
    @Operation(summary = "카풀 목록조회 ", description = "카풀전체목록을 조회합니다")
    public ResponseEntity<List<CarpoolResponseDTO>> readAllCarpool(){
        return adminService.readAllCarpool();
    }

    @GetMapping("/carpoolsPage")
    @Operation(summary = "카풀 목록조회 페이징", description = "카풀전체목록을 조회합니다(페이징  ex) carpools?page=2&size=5)")
    public ResponseEntity<List<CarpoolResponseDTO>> readAllCarpool(Pageable pageable){
        return adminService.readAllCarpool(pageable);
    }

    @GetMapping("/carpool/report/{id}")
    @Operation(summary = "카풀 신고 조회", description = "입력받은 카풀id에 해당하는 모든 신고목록을 조회합니다.")
    public ResponseEntity<List<ReportResponseDto>> readAllByCarpool(@PathVariable("id") Long id){
        return adminService.readAllByCarpool(id);
    }


    //신고내용확인
    @GetMapping("/reports")
    @Operation(summary = "신고 접수 모아보기", description = "관리자가 유저가 신고한 전체목록을 확인합니다.")
    public ResponseEntity<List<ReportResponseDto>> readAllReports() {
        return adminService.reportFindAll();

    }
    @GetMapping("/report/{id}")
    @Operation(summary = "신고 내용 상세보기", description = "관리자가 유저가 신고한 목록을 상세보기 합니다.")
    public ResponseEntity<ReportResponseDto> readReport(@PathVariable("id") Long id) {
        return adminService.reportFindById(id);

    }



}
