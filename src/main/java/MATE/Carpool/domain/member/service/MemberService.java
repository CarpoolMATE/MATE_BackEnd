package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.Message;
import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.common.email.EmailService;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.jwt.JwtProvider;
import MATE.Carpool.config.redis.RedisService;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.*;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {



    private final MemberRepository memberRepository;
    private final PKEncryption pkEncryption;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final EmailService emailService;
    private final ApplicationContext applicationContext;
    private final RedisService redisService;

    @Transactional(readOnly = true)
    public ResponseEntity<MemberResponseDto> getMember(CustomUserDetails userDetails){
        return Optional.ofNullable(userDetails.getMember())
                .map(MemberResponseDto::new)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<MemberResponseDto> readOne(Long memberId){
        return memberRepository.findById(memberId)
                .map(MemberResponseDto::new)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    //로그인
    @Transactional
    public ResponseEntity<Message<Object>> signIn(SignInRequestDto requestDto, HttpServletResponse response, HttpServletRequest request) throws Exception {
        String memberId = requestDto.getMemberId();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberId, requestDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);


        jwtProvider.createTokenAndSavedTokenHttponly(authentication, response,request, memberId);
        log.info("Standard Request");


        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
      
        Member member = customUserDetails.getMember();
 
        MemberResponseDto memberResponseDto = new MemberResponseDto(member);


        return ResponseEntity.ok(new Message<>("로그인","success",memberResponseDto));
    }

    //회원가입
    @Transactional
    public ResponseEntity<String> signUp(SignupRequestDto requestDto) {

        String memberId = requestDto.getMemberId();
        String password = passwordEncoder.encode(requestDto.getPassword());
        boolean checkId = memberRepository.existsByMemberId(memberId);

        if (checkId) {
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER_ID);
        }

        Member member = Member.builder()
                .memberId(requestDto.getMemberId())
                .email(requestDto.getEmail())
                .password(password)
                .nickname(requestDto.getNickname())
                .isUniversity(true)
                .university(requestDto.getUniversity())
                .build();

        memberRepository.save(member);

        return ResponseEntity.ok("회원가입 성공");
    }

    @Transactional
    public ResponseEntity<MemberResponseDto> socialMemberRegisterUniversity(CustomUserDetails userDetails,String university){

        return Optional.ofNullable(userDetails.getMember())
                .map(member ->{
                    member.setUniversity(university);
                    member.setIsUniversity(true);
                    return member;
                })
                .map(memberRepository::save)
                .map(MemberResponseDto::new)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

  
    @Transactional
    public ResponseEntity<MemberResponseDto> registerDriver(CustomUserDetails userDetails,DriverRequestDto driverRequestDto) {

        Member member = userDetails.getMember();

        member.setIsDriver(true);
        member.setCarNumber(driverRequestDto.getCarNumber());
        member.setPhoneNumber(driverRequestDto.getPhoneNumber());
        member.setCarImage(driverRequestDto.getCarImage());
        member.setDriverRegistrationDate(LocalDateTime.now());
        memberRepository.save(member);

        MemberResponseDto responseDto = new MemberResponseDto(member);

        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<MemberResponseDto> cancelDriver(CustomUserDetails userDetails) throws Exception {

        Member member = userDetails.getMember();

        member.setIsDriver(false);
        member.setDriverCancellationDate(LocalDateTime.now());
        memberRepository.save(member);

        MemberResponseDto responseDto = new MemberResponseDto(member);

        return ResponseEntity.ok(responseDto);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Boolean> checkEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        return ResponseEntity.ok(false);
    }

    @Transactional
    public ResponseEntity<String> findPassword(FindPasswordRequestDto requestDto) throws Exception {

        //TODO 사용자가 링크를 통해 비밀번호를 재설정할 수 있는 워크플로를 구현해보기

        Member member = memberRepository.findByMemberId(requestDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(requestDto.getEmail().equals(member.getEmail())) {
            throw new CustomException(ErrorCode.NOT_EQUALS_MEMBER_INFO);
        }
        String newPassword = generateTemporaryPassword();

        member.setPassword(passwordEncoder.encode(newPassword));

        emailService.sendEmailNotice(requestDto.getEmail(),newPassword);

        return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");

    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> findMemberId(FindMemberIdRequestDto requestDto){
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!member.getNickname().equals(requestDto.getNickname())){
            throw new CustomException(ErrorCode.INVALID_NICKNAME);
        }
        return ResponseEntity.ok(Collections.singletonMap("memberId",member.getMemberId()));

    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public ResponseEntity<String> signOut(CustomUserDetails userDetails,HttpServletRequest request, HttpServletResponse response) {
        redisService.deleteRefreshToken(userDetails.getUsername());

        SecurityContextHolder.clearContext();

        jwtProvider.deleteCookie(response, "ACCESS_TOKEN");
        jwtProvider.deleteCookie(response, "REFRESH_TOKEN");

        return ResponseEntity.ok(String.format("%s 회원 로그아웃 완료",userDetails.getMember().getNickname()));
    }



}
