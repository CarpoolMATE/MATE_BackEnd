package MATE.Carpool.domain.member.controller;


import MATE.Carpool.common.Message;
import MATE.Carpool.common.swagger.MemberApi;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.*;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.response.UpdateDriverResponseDto;
import MATE.Carpool.domain.member.dto.response.UpdateMemberResponseDto;
import MATE.Carpool.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController implements MemberApi {

    private final MemberService memberService;


    @GetMapping("")
    public ResponseEntity<MemberResponseDto> getMember(
            @Parameter(description = "회원 ID")
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return memberService.getMember(userDetails);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> readOne(
            @Parameter(description = "회원 ID")
            @PathVariable("memberId") Long memberId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return memberService.readOne(memberId);
    }

    @PostMapping("/signIn")
    public ResponseEntity<Message<Object>> signIn(
            @Valid @RequestBody SignInRequestDto requestDto,
            HttpServletResponse response,
            HttpServletRequest request)  {
        return memberService.signIn(requestDto, response, request);
    }


    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.signUp(requestDto);
    }

    @DeleteMapping("/signOut")
    public ResponseEntity<String> signOut(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response, HttpServletRequest request) {
        return memberService.signOut(userDetails,request,response);
    }

    @Override
    @PostMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@Valid @RequestBody Duplicate.DuplicateEmail email) {
        return memberService.checkEmail(email.email());
    }

    @Override
    @PostMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNickname(@Valid @RequestBody Duplicate.DuplicateNickname nickname) {
        return memberService.checkMemberId(nickname.nickname());
    }

    @Override
    @PostMapping("/checkMemberId")
    public ResponseEntity<Boolean> checkMemberId(@Valid @RequestBody Duplicate.DuplicateMemberId memberId) {
        return memberService.checkMemberId(memberId.memberId());
    }

    @PostMapping("/findPassword")
    public ResponseEntity<String> findPassword(@RequestBody FindPasswordRequestDto requestDto) throws Exception {
        return memberService.findPassword(requestDto);
    }

    @PostMapping("/findMemberId")
    public ResponseEntity<Map<String, String>> findMemberId(@RequestBody FindMemberIdRequestDto findMemberIdRequestDto)  {
        return memberService.findMemberId(findMemberIdRequestDto);
    }

    @PostMapping("/driver")
    public ResponseEntity<MemberResponseDto> registerDriver(
            @RequestBody @Valid DriverRequestDto driverRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return memberService.registerDriver(userDetails, driverRequestDto);
    }

    @PutMapping()
    public ResponseEntity<UpdateMemberResponseDto> updateUser(
            @RequestBody UpdateMemberDTO updateMemberDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return memberService.updateProfileInformation(userDetails, updateMemberDTO);
    }

    @PutMapping("/driver")
    public ResponseEntity<UpdateDriverResponseDto> updateDriver(
            @RequestBody DriverRequestDto driverRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return memberService.updateDriver(driverRequestDto,userDetails);
    }

//    @PostMapping("/cancelDriver/{id}")
//    public ResponseEntity<MemberResponseDto> cancelDriver(@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
//        return memberService.cancelDriver(userDetails);
//    }
//
//    @PostMapping("/registerUniversity")
//    public ResponseEntity<MemberResponseDto> registerUniversity(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody String universityName) {
//        return memberService.socialMemberRegisterUniversity(userDetails, universityName);
//    }





}
