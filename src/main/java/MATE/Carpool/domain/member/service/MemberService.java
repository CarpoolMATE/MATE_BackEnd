package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.common.email.EmailService;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.jwt.JwtProvider;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.FindPasswordRequestDto;
import MATE.Carpool.domain.member.dto.request.SignInRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MemberService {



    private final MemberRepository memberRepository;
    private final PKEncryption pkEncryption;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public ResponseEntity<MemberResponseDto> getMember(CustomUserDetails userDetails){
        return Optional.ofNullable(userDetails.getMember())
                .map(MemberResponseDto::new)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    //로그인
    @Transactional
    public ResponseEntity<Object> signIn(SignInRequestDto requestDto, HttpServletResponse httpServletResponse){
        String memberId = requestDto.getMemberId();

        try {
            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberId, requestDto.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 토큰 생성 및 헤더 설정
            jwtProvider.createTokenAndSavedRefresh(authentication, httpServletResponse, memberId);

            // 인증된 Member 객체 가져오기
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = customUserDetails.getMember();


            // 응답 DTO 생성
            MemberResponseDto memberResponseDto = new MemberResponseDto(member);

            return ResponseEntity.ok(memberResponseDto);

        }  catch (Exception e) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND); // 사용자 정보 없음
        }

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
                .build();

        memberRepository.save(member);

        return ResponseEntity.ok("회원가입 성공");
    }

    //드라이버등록
    @Transactional
    public ResponseEntity<MemberResponseDto> registerDriver(DriverRequestDto driverRequestDto,CustomUserDetails userDetails) {

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

    //이메일중복체크
    @Transactional(readOnly = true)
    public ResponseEntity<Boolean> checkEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        return ResponseEntity.ok(false);
    }

    //비밀번호 찾기
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

    //아이디찾기
    @Transactional(readOnly = true)
    public ResponseEntity<String> findMemberId(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ResponseEntity.ok(member.getMemberId());
    }


    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }




}
