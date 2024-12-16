package MATE.Carpool.domain.admin.controller;

import MATE.Carpool.domain.admin.service.AdminService;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> isBanned(@PathVariable("memberId") String memberId) {
        return adminService.isBanned(memberId);
    }



}
