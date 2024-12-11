package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.config.securityConfig.jwt.JwtProvider;
import MATE.Carpool.config.securityConfig.jwt.JwtTokenDto;
import MATE.Carpool.config.securityConfig.jwt.RefreshToken;
import MATE.Carpool.config.securityConfig.jwt.RefreshTokenRepository;
import MATE.Carpool.config.securityConfig.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.MemberRequestDto;
import MATE.Carpool.domain.member.dto.request.SignInRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.entity.Driver;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.DriverRepository;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.server.ResponseStatusException;

import java.security.InvalidParameterException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Value("${jwt.refresh.time}")
    private long refreshTimeMillis;

    private final MemberRepository memberRepository;
    private final DriverRepository driverRepository;
    private final PKEncryption pkEncryption;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;


    public void encrypt(Long number) throws Exception {
        String encryption = pkEncryption.encrypt(number);
        String decryption = pkEncryption.decrypt(encryption);
        System.out.println(encryption);
        System.out.println(decryption);
    }

    //단일멤버조회
    public ResponseEntity<MemberResponseDto> getMember(String id) {
        return null;
    }

    @Transactional
    public ResponseEntity<Object> signIn(SignInRequestDto requestDto, HttpServletResponse httpServletResponse) throws Exception {
        String memberId = requestDto.getMemberId();

        try {
            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, requestDto.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 토큰 생성 및 헤더 설정
            JwtTokenDto token = jwtProvider.createAllToken(authentication);
            jwtProvider.accessTokenSetHeader(token.getAccessToken(), httpServletResponse);
            jwtProvider.refreshTokenSetHeader(token.getRefreshToken(), httpServletResponse);

            // RefreshToken 저장
            RefreshToken refreshToken = RefreshToken.builder()
                    .refreshToken(token.getRefreshToken())
                    .memberId(memberId)
                    .expiresAt(refreshTimeMillis)
                    .build();
            refreshTokenRepository.save(refreshToken);

            // 인증된 Member 객체 가져오기
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = customUserDetails.getMember();

            // ID 암호화
            String encryption = pkEncryption.encrypt(member.getId());

            // 응답 DTO 생성
            MemberResponseDto memberResponseDto = new MemberResponseDto(encryption, member);

            return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);

        } catch (BadCredentialsException e) {
            // 비밀번호가 잘못된 경우 401 Unauthorized
            return new ResponseEntity<>("일치하지 않는 정보입니다.",HttpStatus.UNAUTHORIZED);
        }
    }

    //회원가입
    @Transactional
    public ResponseEntity<String> signUp(SignupRequestDto requestDto) {

        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());
        Optional<Member> findEmail = memberRepository.findByEmail(email);

        if (findEmail.isPresent()) {
            throw new EntityExistsException("이미 존재하는 이메일입니다. email : " + email);
        }

        Member member = Member.builder()
                .memberId(requestDto.getMemberId())
                .email(requestDto.getEmail())
                .password(password)
                .nickname(requestDto.getNickname())
                .build();

        memberRepository.save(member);



        return new ResponseEntity<>("회원가입 성공",HttpStatus.OK);

    }


    //드라이버등록
    @Transactional
    public ResponseEntity<MemberResponseDto> signUpDriver(DriverRequestDto driverRequestDto) throws Exception {

        Long memberId = decryption(driverRequestDto.getMemberId());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."));

        Driver driver = Driver.builder()
                .carNumber(driverRequestDto.getCarNumber())
                .phoneNumber(driverRequestDto.getPhoneNumber())
                .carImage(driverRequestDto.getCarImage())
                .member(member)
                .build();

        member.setIsDriver(true);
        //@Transactional에서 proxy객체로 넘어감, proxy객체에서 영속성으로 마지막에 밀어넣음

        driverRepository.save(driver);

        MemberResponseDto responseDto = new MemberResponseDto(driverRequestDto.getMemberId(),member);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private Long decryption(String memberId) throws Exception {
        String decryptedValue = pkEncryption.decrypt(memberId);  // 복호화된 값
        try {
            // 복호화된 값을 Long 타입으로 형변환
            return Long.parseLong(decryptedValue);
        } catch (NumberFormatException e) {
            throw new Exception("복호화된 값이 숫자 형식이 아닙니다.", e);  // 예외 처리
        }
    }

}
