package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.Message;
import MATE.Carpool.common.email.EmailService;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.common.generator.VerificationCodeGenerator;
import MATE.Carpool.config.jwt.JwtProvider;
import MATE.Carpool.config.redis.RedisService;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.S3.service.AwsS3Service;
import MATE.Carpool.domain.member.dto.request.*;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.response.UpdateDriverResponseDto;
import MATE.Carpool.domain.member.dto.response.UpdateMemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final EmailService emailService;
    private final RedisService redisService;
    private final AwsS3Service awsS3Service;

    @Transactional(readOnly = true)
    public ResponseEntity<Message<MemberResponseDto>> getMember(CustomUserDetails userDetails){
        return Optional.ofNullable(userDetails.getMember())
                .map(MemberResponseDto::new)
                .map(data ->new Message<>("조회성공",HttpStatus.OK,data))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message<MemberResponseDto>> readOne(Long memberId){
        return memberRepository.findById(memberId)
                .map(MemberResponseDto::new)
                .map(data ->new Message<>("조회성공",HttpStatus.OK,data))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    //로그인
    @Transactional
    public ResponseEntity<Message<Object>> signIn(SignInRequestDto requestDto, HttpServletResponse response, HttpServletRequest request)  {
        String memberId = requestDto.getMemberId();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberId, requestDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

//        jwtProvider.createTokenAndSavedTokenHttponly(authentication, response,request, memberId);
        jwtProvider.createTokenAndSaved(authentication, response,request);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
      
        Member member = customUserDetails.getMember();
 
        MemberResponseDto memberResponseDto = new MemberResponseDto(member);


        return ResponseEntity.ok(new Message<>("로그인",HttpStatus.OK,memberResponseDto));
    }

    //회원가입
    @Transactional
    public ResponseEntity<Message<Boolean>> signUp(SignupRequestDto requestDto) {

        String memberId = requestDto.getMemberId();
        String password = passwordEncoder.encode(requestDto.getPassword());
        boolean checkId = memberRepository.existsByMemberId(memberId);
        boolean checkEmail = memberRepository.existsByEmail(requestDto.getEmail());
        boolean checkNickname = memberRepository.existsByNickname(requestDto.getNickname());

        if (checkId) {
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER_ID);
        }
        if(checkEmail){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if(checkNickname){
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        Member member = Member.builder()
                .memberId(requestDto.getMemberId())
                .email(requestDto.getEmail())
                .password(password)
                .nickname(requestDto.getNickname())
                .build();

        memberRepository.save(member);

        return ResponseEntity.ok(new Message<>("회원가입 성공",HttpStatus.CREATED,true));
    }


  
    @Transactional
    public ResponseEntity<Message<MemberResponseDto>> registerDriver(CustomUserDetails userDetails,DriverRequestDto driverRequestDto) {

        Member member = userDetails.getMember();
        if(member.getIsDriver()){
            throw new CustomException(ErrorCode.ALREADY_IS_DRIVER);
        }

        member.setIsDriver(true);
        member.setCarNumber(driverRequestDto.getCarNumber());
        member.setPhoneNumber(driverRequestDto.getPhoneNumber());
        member.setCarImage(driverRequestDto.getCarImage());
        member.setDriverRegistrationDate(LocalDateTime.now());
        memberRepository.save(member);

        MemberResponseDto responseDto = new MemberResponseDto(member);

        return ResponseEntity.ok(new Message<>("드라이버 등록 성공",HttpStatus.OK,responseDto));
    }

    @Transactional
    public ResponseEntity<Message<UpdateMemberResponseDto>> updateProfileInformation(CustomUserDetails userDetails, UpdateMemberDTO updateMemberDTO) {
        Member member = userDetails.getMember();

        // 기존 프로필 이미지 가져오기
        String currentProfileImage = member.getProfileImage();

        // 새로운 프로필 이미지 가져오기
        String newProfileImage = updateMemberDTO.getProfileImage();

        // 기본 프로필 이미지
        String defaultProfileImage = "https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png";

        // 새로운 이미지가 없거나, 기존 이미지와 동일하면 변경하지 않음
        if (newProfileImage == null || newProfileImage.isEmpty() || newProfileImage.equals(currentProfileImage)) {
            member.setNickname(updateMemberDTO.getNickname()); // 닉네임만 변경
            memberRepository.save(member);
            return ResponseEntity.ok(new Message<>("프로필 수정", HttpStatus.OK, new UpdateMemberResponseDto(member)));
        }

        // 기존 이미지가 기본 프로필이 아닐 경우만 삭제
        if (!currentProfileImage.equals(defaultProfileImage)) {
            awsS3Service.deleteImg(currentProfileImage);
        }

        // 새 프로필 이미지 업데이트
        member.setProfileImage(newProfileImage);
        member.setNickname(updateMemberDTO.getNickname());

        // 변경사항 저장
        memberRepository.save(member);

        return ResponseEntity.ok(new Message<>("프로필 수정 성공", HttpStatus.OK, new UpdateMemberResponseDto(member)));
    }


    @Transactional
    public ResponseEntity<Message<UpdateDriverResponseDto>> updateDriver(DriverRequestDto driverRequestDto,CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        member.setCarNumber(driverRequestDto.getCarNumber());
        member.setPhoneNumber(driverRequestDto.getPhoneNumber());

        if (driverRequestDto.getCarImage() != null && !driverRequestDto.getCarImage().isEmpty()){
            member.setCarImage(driverRequestDto.getCarImage());
        }

        memberRepository.save(member);


        return ResponseEntity.ok(new Message<>("드라이버 수정 성공",HttpStatus.OK,new UpdateDriverResponseDto(member)));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message<Boolean>> checkEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        return ResponseEntity.ok(new Message<>("이메일 체크", HttpStatus.OK,true));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message<Boolean>> checkNickname(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        return ResponseEntity.ok(new Message<>("닉네임 체크", HttpStatus.OK,true));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message<Boolean>> checkMemberId(String memberId) {
        boolean exists = memberRepository.existsByMemberId(memberId);
        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_MEMBER_ID);
        }
        return ResponseEntity.ok(new Message<>("아이디 체크", HttpStatus.OK,true));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message<String>> findMemberId(FindMemberIdRequestDto requestDto){
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!member.getNickname().equals(requestDto.getNickname())){
            throw new CustomException(ErrorCode.INVALID_NICKNAME);
        }
        return ResponseEntity.ok(new Message<>("멤버아이디 조회 성공",HttpStatus.OK,member.getMemberId()));

    }

    public ResponseEntity<Message<String>> signOut(CustomUserDetails userDetails,HttpServletRequest request, HttpServletResponse response) {
        redisService.deleteRefreshToken(userDetails.getUsername());

        SecurityContextHolder.clearContext();

        jwtProvider.deleteCookie(response, "ACCESS_TOKEN");
        jwtProvider.deleteCookie(response, "REFRESH_TOKEN");

        return ResponseEntity.ok(new Message<>(String.format("%s 회원 로그아웃 완료",userDetails.getMember().getNickname()),HttpStatus.OK,""));
    }


    @Transactional
    public ResponseEntity<Message<Boolean>> findPassword(GetMemberInfo.ForgotPassword memberInfo) throws MessagingException {

        Member member = memberRepository.findByMemberId(memberInfo.memberId())
                .orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!member.getEmail().equals(memberInfo.email())){
            throw new CustomException(ErrorCode.INVALID_EMAIL);
        }

        String newPassword = VerificationCodeGenerator.generatePassword();
        member.setPassword(passwordEncoder.encode(newPassword));

        emailService.sendEmailForgotPassword(memberInfo.email(),newPassword);

        return ResponseEntity.ok(new Message<>("이메일이 발송되었습니다.",HttpStatus.OK,true));
    }



    @Transactional
    public ResponseEntity<Message<Boolean>> changePassword(CustomUserDetails userDetails,GetMemberInfo.Password password) {

        Member member = userDetails.getMember();

        member.setPassword(passwordEncoder.encode(password.password()));
        memberRepository.save(member);

        return ResponseEntity.ok(new Message<>("비밀번호가 변경되었습니다.",HttpStatus.OK,true));
    }


    public ResponseEntity<Message<Boolean>> checkPassword(CustomUserDetails userDetails, GetMemberInfo.Password password) {
        Member member = userDetails.getMember();

        if(passwordEncoder.matches(member.getPassword(),password.password())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);

        }
        return ResponseEntity.ok(new Message<>("비밀번호 확인 완료",HttpStatus.OK,true));
    }
}
