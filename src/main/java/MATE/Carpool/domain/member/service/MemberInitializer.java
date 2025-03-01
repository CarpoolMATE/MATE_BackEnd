  package MATE.Carpool.domain.member.service;

  import MATE.Carpool.domain.member.entity.Member;
  import MATE.Carpool.domain.member.entity.MemberType;
  import MATE.Carpool.domain.member.repository.MemberRepository;
  import lombok.RequiredArgsConstructor;
  import org.springframework.boot.CommandLineRunner;
  import org.springframework.security.crypto.password.PasswordEncoder;
  import org.springframework.stereotype.Service;
  import org.springframework.web.bind.annotation.RestController;

  import java.util.ArrayList;
  import java.util.List;

  @Service
  @RequiredArgsConstructor
  public class MemberInitializer implements CommandLineRunner {

      private final MemberRepository memberRepository;
      private final PasswordEncoder passwordEncoder;
      @Override
      public void run(String... args) throws Exception {

          if(!memberRepository.existsByMemberId("admin")){
              Member admin = Member.builder()
                      .memberId("admin")
                      .email("admin@test.com")
                      .password(passwordEncoder.encode("1234a"))
                      .nickname("admin")
                      .memberType(MemberType.ADMIN)
                      .build();

              memberRepository.save(admin);
          }

          List<Member> members =new ArrayList<>();
          long memberSize = memberRepository.count();
          int memberCount = (int) memberSize;
          if(memberCount < 40){
              for (int i = memberCount; i < memberCount+40; i++) {
                  if(i % 2 == 0){
                      Member member = Member.builder()
                              .memberId("tester"+i)
                              .email("tester"+i+"@test.com")
                              .password(passwordEncoder.encode("1234a"))
                              .nickname("tester"+i)
                              .memberType(MemberType.STANDARD)
                              .build();
                      members.add(member);
                  }else{
                      Member member = Member.builder()
                              .memberId("tester"+i)
                              .email("tester"+i+"@test.com")
                              .password(passwordEncoder.encode("1234a"))
                              .nickname("tester"+i)
                              .memberType(MemberType.STANDARD)

                              .isDriver(true)
                              .carNumber(i+"가 1234")
                              .phoneNumber("010-1234-12"+i)
                              .carImage("https://carool-s3.s3.ap-northeast-2.amazonaws.com/profileImgS3.png")

                              .build();
                      members.add(member);

                  }

              }
              memberRepository.saveAll(members);

          }

      }
  }
