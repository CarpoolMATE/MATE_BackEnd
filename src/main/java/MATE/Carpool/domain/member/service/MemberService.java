package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.MemberRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.entity.Driver;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.DriverRepository;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final DriverRepository driverRepository;
    private final PKEncryption pkEncryption;
    private final PasswordEncoder passwordEncoder;


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

    //로그인
    public ResponseEntity<MemberResponseDto> signIn(MemberRequestDto memberRequestDto) {
        return null;
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
