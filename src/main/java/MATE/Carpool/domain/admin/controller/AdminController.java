package MATE.Carpool.domain.admin.controller;

import MATE.Carpool.domain.admin.service.AdminService;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    //회원 전체조회
    @GetMapping()
    public ResponseEntity<List<MemberResponseDto>> readAll(){
        return adminService.readAll();
    }


}
