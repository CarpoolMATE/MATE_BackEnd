package MATE.Carpool.domain.member.service;

import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.domain.member.dto.request.DriverRequestDto;
import MATE.Carpool.domain.member.dto.request.SignupRequestDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberInitializer implements CommandLineRunner {

    @Autowired
    private MemberService memberService; // 회원가입 서비스
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) {
        //어드민 가입
        Member member = Member.builder()
                .memberId("admin1")
                .memberType(MemberType.ADMIN)
                .email("admin1@gmail.com")
                .password("admin1")
                .nickname("admin1")
                .build();
        memberRepository.save(member);


    }
}