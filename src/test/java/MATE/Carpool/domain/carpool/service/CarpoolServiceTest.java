package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseListDTO;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CarpoolServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CarpoolRepository carpoolRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void create(){

        Member member = Member.builder()
                .memberId("test1")
                .email("test@test.test")
                .password("1234")
                .nickname("test")
                .profileImage("test")
                .build();
        Member member2 = Member.builder()
                .memberId("test2")
                .email("test2@test.test")
                .password("1234")
                .nickname("test2")
                .profileImage("test2")
                .build();

//        Member member3 = Member.builder()
//                .memberId("test3")
//                .email("test3@test.test")
//                .password("1234")
//                .nickname("test3")
//                .profileImage("test3")
//                .build();

        memberRepository.save(member);
        memberRepository.save(member2);
//        memberRepository.save(member3);

        CarpoolEntity carpool =new CarpoolEntity();
        carpool.setMember(member);
        carpool.setCreatedAt(LocalDateTime.of(2024,12,14,11,00));
        carpool.setCapacity(4);
        carpool.setReservationCount(2);
        carpool.setCost(1000);
        carpool.setDepartureCoordinate("123");
        carpoolRepository.save(carpool);


        CarpoolEntity carpool2 =new CarpoolEntity();
        carpool2.setMember(member2);
        carpool2.setCreatedAt(LocalDateTime.of(2024,12,14,12,00));
        carpool2.setCapacity(4);
        carpool2.setReservationCount(2);
        carpool2.setCost(1000);
        carpool2.setDepartureCoordinate("123");
        carpoolRepository.save(carpool2);


        CarpoolEntity carpool3 =new CarpoolEntity();
        carpool3.setMember(member);
        carpool3.setCreatedAt(LocalDateTime.of(2024,12,15,11,00));
        carpool3.setCapacity(4);
        carpool3.setReservationCount(2);
        carpool3.setCost(1000);
        carpool3.setDepartureCoordinate("123");
        carpoolRepository.save(carpool3);

//        entityManager.flush();


//        LocalDateTime blockStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(10,0));
//        if (LocalDateTime.now().isBefore(blockStart)) {
//            blockStart = blockStart.minusDays(1);
//        }
        LocalDateTime blockStart = LocalDateTime.of(2024,12,15,10,30);


        List<CarpoolEntity> list = carpoolRepository.findAll();
        for(CarpoolEntity entity : list){
            System.out.println(entity.getCreatedAt());
        }


        List<CarpoolResponseListDTO> carpoolList =
                carpoolRepository.findByAllList(blockStart)
                        .stream()
                        .map(CarpoolResponseListDTO::new)
                        .toList();

        assertNotNull(carpoolList);

//        assertEquals(1,carpoolList.size());
        assertEquals(1,carpoolList.size());
    }

}