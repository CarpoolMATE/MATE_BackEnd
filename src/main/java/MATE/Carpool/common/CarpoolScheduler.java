//package MATE.Carpool.common;
//
//import MATE.Carpool.common.exception.CustomException;
//import MATE.Carpool.config.userDetails.CustomUserDetails;
//import MATE.Carpool.domain.carpool.dto.request.ReservationCarpoolRequestDTO;
//import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
//import MATE.Carpool.domain.carpool.service.CarpoolRequestDtoGenerator;
//import MATE.Carpool.domain.carpool.service.CarpoolService;
//import MATE.Carpool.domain.member.entity.Member;
//import MATE.Carpool.domain.member.repository.MemberRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//
//import static MATE.Carpool.domain.carpool.service.CarpoolRequestDtoGenerator.generateRandomCarpoolRequestDTO;
//import static MATE.Carpool.domain.carpool.service.CarpoolService.getBlockStart;
//
//@RequiredArgsConstructor
//@Component
//@Slf4j
//public class CarpoolScheduler {
//
//    private final MemberRepository memberRepository;
//    private final CarpoolRepository carpoolRepository;
//    private final CarpoolService carpoolService;
//
//
//
//    @Scheduled(cron = "0 5 10 * * ?")
//    public void autoCarpoolGenerator() {
//
//        log.info("Carpool DummyData Generator process start");
//
//
//        List<Member> members = memberRepository.findTesterMember("tester");
//
//        List<CustomUserDetails> drivers =
//                members.stream()
//                        .filter(Member::getIsDriver)
//                        .map(CustomUserDetails::new)
//                        .limit(7)
//                        .toList();
//
//        List<CustomUserDetails> standardMembers =
//                members.stream()
//                        .filter(member -> !drivers.stream()
//                                .map(cm -> cm.getMember().getId())
//                                .toList()
//                                .contains(member.getId()))
//                        .map(CustomUserDetails::new)
//                        .toList();
//
//        for (int i = 0; i < drivers.size(); i++) {
//            carpoolService.makeCarpool(drivers.get(i), generateRandomCarpoolRequestDTO());
//        }
//
//        List<Long> carpools = carpoolRepository.findAllTodayList(getBlockStart());
//        for (int i = 0; i < standardMembers.size(); i++) {
//
//            Long carpoolId = carpools.get(ThreadLocalRandom.current().nextInt(carpools.size()));
//            ReservationCarpoolRequestDTO requestDTO = new ReservationCarpoolRequestDTO(carpoolId);
//            try {
//                carpoolService.reservationCarpool(standardMembers.get(i), requestDTO);
//            } catch (CustomException e) {
//                e.getErrorCode();
//            } finally {
//                continue;
//            }
//
//
//        }
//
//        log.info("Carpool DummyData Generator process end");
//
//    }
//}
//
