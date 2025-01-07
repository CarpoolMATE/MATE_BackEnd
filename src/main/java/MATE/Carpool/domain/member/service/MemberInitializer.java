package MATE.Carpool.domain.member.service;

import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberInitializer implements CommandLineRunner {

    @Autowired
    private MemberService memberService; // 회원가입 서비스
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

//        try{
//            Member member = memberRepository.findByMemberId("admin1").orElse(null);
//            assert member != null;
//            memberRepository.delete(member);
//            memberRepository.save(member);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }

        //어드민 가입
        Member member = Member.builder()
                .memberId("admin1")
                .memberType(MemberType.ADMIN)
                .email("admin1@gmail.com")
                .password(passwordEncoder.encode("admin1"))
                .nickname("admin1")
                .build();
        memberRepository.save(member);


    }
}