package MATE.Carpool.domain.member.controller;


import MATE.Carpool.common.Message;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.*;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("")
    @Operation(summary = "회원 조회", description = "주어진 ID로 회원 정보를 조회합니다.")
    public ResponseEntity<MemberResponseDto> getMember(
            @Parameter(description = "회원 ID")
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return memberService.getMember(userDetails);
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "회원 조회", description = "주어진 ID로 회원 정보를 조회합니다.")
    public ResponseEntity<MemberResponseDto> readOne(
            @Parameter(description = "회원 ID")
            @PathVariable("memberId") Long memberId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return memberService.readOne(memberId);
    }

    @PostMapping("/signIn")
    @Operation(summary = "일반로그인", description = "사용자가 아이디와 비밀번호를 입력하여 로그인합니다.(일반 로그인 입니다)")
    public ResponseEntity<Message<Object>> signIn(
            @Valid @RequestBody SignInRequestDto requestDto,
            HttpServletResponse response,
            HttpServletRequest request) throws Exception {
        return memberService.signIn(requestDto, response, request);
    }


    @PostMapping("/signUp")
    @Operation(summary = "회원가입", description = "새로운 회원을 가입시킵니다.")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.signUp(requestDto);
    }

    @DeleteMapping("/signOut")
    @Operation(summary = "로그아웃", description = "로그아웃 메서드입니다. 쿠키를 모두 삭제합니다.")
    public ResponseEntity<String> signOut(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response, HttpServletRequest request) {
        return memberService.signOut(userDetails,request,response);
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
    public ResponseEntity<Map<String, String>> findMemberId(@RequestBody FindMemberIdRequestDto findMemberIdRequestDto)  {
        return memberService.findMemberId(findMemberIdRequestDto);
    }

    @PostMapping("/driver")
    @Operation(summary = "운전기사 등록", description = "운전기사로 등록합니다.")
    public ResponseEntity<MemberResponseDto> registerDriver(
            @RequestBody DriverRequestDto driverRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return memberService.registerDriver(userDetails, driverRequestDto);
    }

    @PostMapping("/cancelDriver/{id}")
    @Operation(summary = "운전기사 해제", description = "운전기사자격을 해제합니다.")
    public ResponseEntity<MemberResponseDto> cancelDriver(@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        return memberService.cancelDriver(userDetails);
    }

    @PostMapping("/registerUniversity")
    public ResponseEntity<MemberResponseDto> registerUniversity(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody String universityName) {
        return memberService.socialMemberRegisterUniversity(userDetails, universityName);
    }





}
