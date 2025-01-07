package MATE.Carpool.domain.member.controller;


import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.FindPasswordRequestDto;
import MATE.Carpool.domain.member.dto.request.SignInRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import MATE.Carpool.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/test")
    public ResponseEntity<List<String>> test() {
        return ResponseEntity.ok(memberRepository.findAll().stream().map(Member::getMemberId).collect(Collectors.toList()));

    }


    @PostMapping("/signIn")
    @Operation(summary = "로그인", description = "사용자가 아이디와 비밀번호를 입력하여 로그인합니다.")
    public ResponseEntity<Object> signIn(
            @Valid @RequestBody SignInRequestDto requestDto,
            HttpServletResponse httpServletResponse) throws Exception {
        return memberService.signIn(requestDto, httpServletResponse);
    }

    @PostMapping("/signUp")
    @Operation(summary = "회원가입", description = "새로운 회원을 가입시킵니다.")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.signUp(requestDto);
    }

    @PostMapping("/checkEmail")
    @Operation(summary = "이메일 중복확인", description = "이메일이 이미 존재하는지 확인합니다.")
    public ResponseEntity<Boolean> checkEmail(@Valid @RequestBody String email) {
        return memberService.checkEmail(email);
    }
    @PostMapping("/findPassword")
    @Operation(summary = "비밀번호 찾기", description = "가입한 아이디와 이메일을통해 비밀번호를 찾습니다. 이메일로 발송됩니다.")
    public ResponseEntity<String> findPassword(@RequestBody FindPasswordRequestDto requestDto) throws Exception {
        return memberService.findPassword(requestDto);

    }
    @PostMapping("/findMemberId")
    @Operation(summary = "아이디 찾기", description = "가입한 이메일을 통해 아이디를 찾습니다.")
    public ResponseEntity<String> findMemberId(@RequestBody String email)  {
        return memberService.findMemberId(email);
    }

    @GetMapping("")
    @Operation(summary = "회원 조회", description = "주어진 ID로 회원 정보를 조회합니다.")
    public ResponseEntity<MemberResponseDto> getMember(
            @Parameter(description = "회원 ID")
           @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        return memberService.getMember(userDetails);
    }

    @PostMapping("/driver")
    @Operation(summary = "운전기사 등록", description = "운전기사로 등록합니다.")
    public ResponseEntity<MemberResponseDto> registerDriver(
            @RequestBody DriverRequestDto driverRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        return memberService.registerDriver(driverRequestDto,userDetails);
    }

    @PostMapping("/cancelDriver/{id}")
    @Operation(summary = "운전기사 해제", description = "운전기사자격을 해제합니다.")
    public ResponseEntity<MemberResponseDto> cancelDriver(@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        return memberService.cancelDriver(userDetails);
    }



}
