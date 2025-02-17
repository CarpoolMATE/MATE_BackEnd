 package MATE.Carpool.domain.member.service;

 import MATE.Carpool.domain.member.entity.Member;
 import MATE.Carpool.domain.member.entity.MemberType;
 import MATE.Carpool.domain.member.repository.MemberRepository;
 import lombok.RequiredArgsConstructor;
 import org.springframework.boot.CommandLineRunner;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.stereotype.Service;
 import org.springframework.web.bind.annotation.RestController;

 @Service
 @RequiredArgsConstructor
 public class MemberInitializer implements CommandLineRunner {

     private final MemberRepository memberRepository;
     private final PasswordEncoder passwordEncoder;
     @Override
     public void run(String... args) throws Exception {
         Member admin = Member.builder()
                 .memberId("admin")
                 .email("admin@test.com")
                 .password(passwordEncoder.encode("1234a"))
                 .nickname("admin")
                 .isUniversity(true)
                 .university("서울대학교")
                 .memberType(MemberType.ADMIN)
                 .build();

         memberRepository.save(admin);


         Member member = Member.builder()
                 .memberId("testa")
                 .email("testa@test.com")
                 .password(passwordEncoder.encode("1234a"))
                 .nickname("aaaa")
                 .isUniversity(true)
                 .university("서울대학교")
                 .build();

         memberRepository.save(member);

         Member member2 = Member.builder()
                 .memberId("testb")
                 .email("testb@test.com")
                 .password(passwordEncoder.encode("1234a"))
                 .nickname("bbbb")
                 .isUniversity(true)
                 .university("서울대학교")
                 .build();

         memberRepository.save(member2);
     }
 }
