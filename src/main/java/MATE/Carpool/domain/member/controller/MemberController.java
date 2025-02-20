package MATE.Carpool.domain.member.controller;


import MATE.Carpool.common.Message;
import MATE.Carpool.common.swagger.MemberApi;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.*;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.response.ResetPasswordResponse;
import MATE.Carpool.domain.member.dto.response.UpdateDriverResponseDto;
import MATE.Carpool.domain.member.dto.response.UpdateMemberResponseDto;
import MATE.Carpool.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Message<MemberResponseDto>> getMember(
            @Parameter(description = "회원 ID")
            @AuthenticationPrincipal CustomUserDetails userDetails){
        return memberService.getMember(userDetails);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<Message<MemberResponseDto>> readOne(
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message<Boolean>> signUp(@Valid @RequestBody SignupRequestDto requestDto) {
        return memberService.signUp(requestDto);
    }

    @DeleteMapping("/signOut")
    public ResponseEntity<Message<String>> signOut(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response, HttpServletRequest request) {
        return memberService.signOut(userDetails,request,response);
    }

    @Override
    @PostMapping("/checkEmail")
    public ResponseEntity<Message<Boolean>> checkEmail(@Valid @RequestBody GetMemberInfo.DuplicateEmail email) {
        return memberService.checkEmail(email.email());
    }

    @Override
    @PostMapping("/checkNickname")
    public ResponseEntity<Message<Boolean>> checkNickname(@Valid @RequestBody GetMemberInfo.DuplicateNickname nickname) {
        return memberService.checkMemberId(nickname.nickname());
    }

    @Override
    @PostMapping("/checkMemberId")
    public ResponseEntity<Message<Boolean>> checkMemberId(@Valid @RequestBody GetMemberInfo.DuplicateMemberId memberId) {
        return memberService.checkMemberId(memberId.memberId());
    }

    @PostMapping("/findMemberId")
    public ResponseEntity<Message<String>> findMemberId(@RequestBody FindMemberIdRequestDto findMemberIdRequestDto)  {
        return memberService.findMemberId(findMemberIdRequestDto);
    }

    @PostMapping("/driver")
    public ResponseEntity<Message<MemberResponseDto>> registerDriver(
            @RequestBody @Valid DriverRequestDto driverRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return memberService.registerDriver(userDetails, driverRequestDto);
    }

    @PutMapping("/member")
    public ResponseEntity<Message<UpdateMemberResponseDto>> updateUser(
            @RequestBody UpdateMemberDTO updateMemberDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return memberService.updateProfileInformation(userDetails, updateMemberDTO);
    }

    @PutMapping("/driver")
    public ResponseEntity<Message<UpdateDriverResponseDto>> updateDriver(
            @RequestBody DriverRequestDto driverRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return memberService.updateDriver(driverRequestDto,userDetails);
    }

    @PostMapping("/findPassword")
    public ResponseEntity<Message<Boolean>> findPassword(
            @RequestBody GetMemberInfo.ForgotPassword memberInfo)
            throws MessagingException {
        return memberService.findPassword(memberInfo);

    }

    @PostMapping("/changePassword")
    public ResponseEntity<Message<Boolean>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody GetMemberInfo.Password password){
        return memberService.changePassword(userDetails,password);

    }

    @PostMapping("/checkPassword")
    public ResponseEntity<Message<Boolean>> checkPassword(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody GetMemberInfo.Password password) {
        return memberService.checkPassword(userDetails,password);
    }



}
