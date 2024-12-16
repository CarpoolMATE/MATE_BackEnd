package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.response.PassengerInfoDTO;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.carpool.repository.ReservationRepository;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CarpoolServiceTest {

    @Autowired
    private CarpoolRepository carpoolRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CarpoolService carpoolService;

    private Member driver;
    private Member passenger1;
    private Member passenger2;
    private CarpoolEntity carpool;



    @BeforeEach
    @Transactional
    void setUp() {
        // Driver 생성
        driver = Member.builder()
                .memberId("driver123")
                .nickname("Driver")
                .email("driver@example.com")
                .password("password")
                .profileImage("driver_image.png")
                .isDriver(true)
                .build();
        memberRepository.save(driver);

        // Passenger1 생성
        passenger1 = Member.builder()
                .memberId("passenger1")
                .nickname("Passenger1")
                .email("passenger1@example.com")
                .password("password")
                .profileImage("passenger1_image.png")
                .build();
        memberRepository.save(passenger1);

        // Passenger2 생성
        passenger2 = Member.builder()
                .memberId("passenger2")
                .nickname("Passenger2")
                .email("passenger2@example.com")
                .password("password")
                .profileImage("passenger2_image.png")
                .build();
        memberRepository.save(passenger2);

        // Carpool 생성
        carpool = new CarpoolEntity();
        carpool.setMember(driver);
        carpool.setCapacity(4);
        carpool.setReservationCount(2);
        carpool.setCost(1000);
        carpool.setDepartureCoordinate("123.456");
        carpool.setDepartureDateTime(LocalDateTime.of(2024, 12, 15, 10, 30));
        carpoolRepository.save(carpool);

        // Reservation 생성
        ReservationEntity reservation1 = new ReservationEntity();
        reservation1.setCarpool(carpool);
        reservation1.setMember(passenger1);
        reservationRepository.save(reservation1);

        ReservationEntity reservation2 = new ReservationEntity();
        reservation2.setCarpool(carpool);
        reservation2.setMember(passenger2);
        reservationRepository.save(reservation2);

        // EntityManager flush
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @Transactional
    void testMyCarpool() throws Exception {
        // Mock User Details
        CustomUserDetails userDetails = new CustomUserDetails( driver );

        // Service method 호출
        ResponseEntity<List<PassengerInfoDTO>> response = carpoolService.myCarpool( carpool.getId());

        // 응답 검증
        assertNotNull(response);
        List<PassengerInfoDTO> passengerList = response.getBody();
        System.out.println(passengerList.get(0).toString());
        System.out.println(passengerList.get(1).toString());
        assertNotNull(passengerList);
        assertEquals(2, passengerList.size());

        // Passenger1 검증
        PassengerInfoDTO passenger1DTO = passengerList.get(0);
        assertEquals("Passenger1", passenger1DTO.getPassengerName());
        assertEquals("passenger1_image.png", passenger1DTO.getPassengerImg());

        // Passenger2 검증
        PassengerInfoDTO passenger2DTO = passengerList.get(1);
        assertEquals("Passenger2", passenger2DTO.getPassengerName());
        assertEquals("passenger2_image.png", passenger2DTO.getPassengerImg());


    }

    /*
    @Test
    @Transactional
    public void create() {

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

        Member member3 = Member.builder()
                .memberId("test3")
                .email("test3@test.test")
                .password("1234")
                .nickname("test3")
                .profileImage("test3")
                .build();

        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);

        CarpoolEntity carpool = new CarpoolEntity();
        carpool.setMember(member);
        carpool.setCreatedAt(LocalDateTime.of(2024, 12, 14, 11, 00));
        carpool.setCapacity(4);
        carpool.setReservationCount(2);
        carpool.setCost(1000);
        carpool.setDepartureCoordinate("123");
        carpoolRepository.save(carpool);


        CarpoolEntity carpool2 = new CarpoolEntity();
        carpool2.setMember(member2);
        carpool2.setCreatedAt(LocalDateTime.of(2024, 12, 14, 12, 00));
        carpool2.setCapacity(4);
        carpool2.setReservationCount(2);
        carpool2.setCost(1000);
        carpool2.setDepartureCoordinate("123");
        carpoolRepository.save(carpool2);


        CarpoolEntity carpool3 = new CarpoolEntity();
        carpool3.setMember(member);
        carpool3.setCreatedAt(LocalDateTime.of(2024, 12, 15, 11, 00));
        carpool3.setCapacity(4);
        carpool3.setReservationCount(2);
        carpool3.setCost(1000);
        carpool3.setDepartureCoordinate("123");
        carpoolRepository.save(carpool3);


    }*/

}