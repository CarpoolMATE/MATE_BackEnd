package MATE.Carpool.domain.member.service;


import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.MemberRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PKEncryption pkEncryption;

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
    public ResponseEntity<MemberResponseDto> signUp(SignupRequestDto requestDto) {
        return null;
    }


    //드라이버등록
    public ResponseEntity<MemberResponseDto> signUpDriver(DriverRequestDto driverRequestDto) {
        return null;
    }
}
