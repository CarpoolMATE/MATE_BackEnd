//package MATE.Carpool.domain.member.service;
//
//import MATE.Carpool.common.PKEncryption;
//import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
//import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
//import MATE.Carpool.domain.member.entity.Member;
//import MATE.Carpool.domain.member.repository.MemberRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//public class MemberInitializer implements CommandLineRunner {
//
//    @Autowired
//    private MemberService memberService; // 회원가입 서비스
//    @Autowired
//    private MemberRepository memberRepository;
//    @Autowired
//    private PKEncryption pkEncryption;
//
//    @Override
//    public void run(String... args) throws Exception {
//        //자동으로 회원가입 실행
//        SignupRequestDto requestDto = new SignupRequestDto(
//                "memberId123",
//                "test@test.com",
//                "tesPassword123!",
//                "테스트"
//        );
//
//        memberService.signUp(requestDto); // signUp 로직을 호출
//
//        Optional<Member> member = memberRepository.findByEmail("test@test.com");
//
//        member.ifPresent(m -> {
//            try {
//                Long memberId = m.getId();
//                String encryptId = pkEncryption.encrypt(memberId);
//
//                DriverRequestDto driverRequestDto = new DriverRequestDto(
//                        encryptId,
//                        "12가3456",
//                        "010-1234-5678",
//                        "carImage.jpg"
//                );
//
//                memberService.signUpDriver(driverRequestDto);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        });
//    }
//}