package MATE.Carpool.domain.member.controller;


import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.MemberRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String index() {
        return "Hello World";
    }
    @GetMapping("test/{number}")
    public void encrypt(@PathVariable Long number) throws Exception {
        memberService.encrypt(number);
    }

    //로그인
    @PostMapping("signIn")
    public ResponseEntity<MemberResponseDto> signIn(@RequestBody MemberRequestDto memberRequestDto) {

        return memberService.signIn(memberRequestDto);
    }
    @PostMapping("signUp")
    public ResponseEntity<MemberResponseDto> signUp(@RequestBody SignupRequestDto requestDto) {
        return memberService.signUp(requestDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable String id) {
        return memberService.getMember(id);
    }
    @PostMapping("driver")
    public ResponseEntity<MemberResponseDto> signUpDriver(@RequestBody DriverRequestDto driverRequestDto) {
        return memberService.signUpDriver(driverRequestDto);
    }

}
