package MATE.Carpool.domain.member.controller;


import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.SignInRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    //로그인
    @PostMapping("signIn")
    @Operation(summary = "로그인", description = "사용자가 아이디와 비밀번호를 입력하여 로그인합니다.")
    public ResponseEntity<Object> signIn(
            @Valid @RequestBody SignInRequestDto requestDto,
            HttpServletResponse httpServletResponse) throws Exception {
        return memberService.signIn(requestDto, httpServletResponse);
    }

    //회원가입
    @PostMapping("signUp")
    @Operation(summary = "회원가입", description = "새로운 회원을 가입시킵니다.")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.signUp(requestDto);
    }

    //이메일 중복확인
    @PostMapping("checkEmail")
    @Operation(summary = "이메일 중복확인", description = "이메일이 이미 존재하는지 확인합니다.")
    public ResponseEntity<Boolean> checkEmail(@Valid @RequestBody String email) {
        return memberService.checkEmail(email);
    }

    //회원조회
    @GetMapping("{id}")
    @Operation(summary = "회원 조회", description = "주어진 ID로 회원 정보를 조회합니다.")
    public ResponseEntity<MemberResponseDto> getMember(
            @Parameter(description = "회원 ID")
            @PathVariable("id") String id) throws Exception {
        return memberService.getMember(id);
    }

    @PostMapping("driver")
    @Operation(summary = "운전기사 등록", description = "운전기사로 등록합니다.")
    public ResponseEntity<MemberResponseDto> signUpDriver(@RequestBody DriverRequestDto driverRequestDto) throws Exception {
        return memberService.signUpDriver(driverRequestDto);
    }
    //회원탈퇴?
    //로그아웃


}
