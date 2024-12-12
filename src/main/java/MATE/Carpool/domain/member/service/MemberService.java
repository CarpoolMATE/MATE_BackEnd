package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.config.jwt.JwtProvider;
import MATE.Carpool.config.jwt.JwtTokenDto;
import MATE.Carpool.config.jwt.RefreshToken;
import MATE.Carpool.config.jwt.RefreshTokenRepository;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.SignInRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.entity.Driver;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.DriverRepository;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {



    private final MemberRepository memberRepository;
    private final DriverRepository driverRepository;
    private final PKEncryption pkEncryption;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    //단일멤버조회
    @Transactional(readOnly = true)
    public ResponseEntity<MemberResponseDto> getMember(String memberId) throws Exception {
        // 회원 조회 로직 (별도 메서드로 분리)
        Member member = findByMember(memberId);
        // ResponseEntity 생성 및 반환
        MemberResponseDto responseDto = new MemberResponseDto(memberId, member);
        return ResponseEntity.ok(responseDto);
    }

    //로그인
    @Transactional
    public ResponseEntity<Object> signIn(SignInRequestDto requestDto, HttpServletResponse httpServletResponse) throws Exception {
        String memberId = requestDto.getMemberId();

        try {
            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, requestDto.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 토큰 생성 및 헤더 설정
            jwtProvider.createTokenAndSavedRefresh(authentication,httpServletResponse,memberId);

            // 인증된 Member 객체 가져오기
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = customUserDetails.getMember();

            // ID 암호화
            String encryption = pkEncryption.encrypt(member.getId());

            // 응답 DTO 생성
            MemberResponseDto memberResponseDto = new MemberResponseDto(encryption, member);

            return ResponseEntity.ok(memberResponseDto);

        } catch (BadCredentialsException e) {
            // 비밀번호가 잘못된 경우 401 Unauthorized

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("일치하지 않는 정보입니다.");
        }
    }

    //회원가입
    @Transactional
    public ResponseEntity<String> signUp(SignupRequestDto requestDto) {

        String memberId = requestDto.getMemberId();
        String password = passwordEncoder.encode(requestDto.getPassword());
        boolean checkId = memberRepository.existsByMemberId(memberId);

        if (checkId) {
            throw new EntityExistsException("이미 존재하는 아이디 입니다. Id : " + memberId);
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
    public ResponseEntity<MemberResponseDto> signUpDriver(DriverRequestDto driverRequestDto) throws Exception {

        Member member = findByMember(driverRequestDto.getMemberId());
        

        Driver driver = Driver.builder()
                .carNumber(driverRequestDto.getCarNumber())
                .phoneNumber(driverRequestDto.getPhoneNumber())
                .carImage(driverRequestDto.getCarImage())
                .member(member)
                .build();

        member.setIsDriver(true);

        driverRepository.save(driver);

        MemberResponseDto responseDto = new MemberResponseDto(driverRequestDto.getMemberId(),member,driver);

        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public Member findByMember(String memberId) throws Exception {
        String decryptedValue = pkEncryption.decrypt(memberId);  // 복호화된 값
        try {
            Long id =Long.parseLong(decryptedValue);
            return  memberRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("회원이 존재하지 않습니다."));
            // 복호화된 값을 Long 타입으로 형변환
        } catch (NumberFormatException e) {
            throw new Exception("복호화된 값이 숫자 형식이 아닙니다.", e);  // 예외 처리
        }
    }

    @Transactional
    public ResponseEntity<Boolean> checkEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        if (exists) {
            throw new EntityNotFoundException("중복된 이메일입니다.");
        }
        return ResponseEntity.ok(false);
    }


}
