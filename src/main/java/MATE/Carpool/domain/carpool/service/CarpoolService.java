package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.request.ReservationCarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.PassengerInfoDTO;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.carpool.repository.ReservationRepository;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor //자동으로 생성자 작성
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;


    @Transactional(readOnly = true)
    public ResponseEntity<List<CarpoolResponseDTO>> GetCarpoolList(){

        LocalDateTime blockStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(10,0));
        if (LocalDateTime.now().isBefore(blockStart)) {
            blockStart = blockStart.minusDays(1);
        }

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByAllList(blockStart);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(carpoolResponseDTOS);

    }

    //내가 등록한 카풀 조회
    @Transactional
    public ResponseEntity<List<PassengerInfoDTO>> myCarpool(Long carpoolId) throws Exception {

        // 해당 카풀 엔티티 조회
        CarpoolEntity carpool = findByCarpool(carpoolId);

        List<ReservationEntity> reservationEntities = reservationRepository.findByCarpool(carpool.getId());

        List<PassengerInfoDTO> passengerInfoDTOS = new ArrayList<>();

        for (ReservationEntity r : reservationEntities) {
            passengerInfoDTOS.add(new PassengerInfoDTO(r.getMember()));
        }

        return ResponseEntity.ok(passengerInfoDTOS);
    }



    //카풀 생성
    public ResponseEntity<String> makeCarpool(CustomUserDetails userDetails,CarpoolRequestDTO carpoolRequestDTO) {

        Member member = userDetails.getMember();

        CarpoolEntity carpool = new CarpoolEntity();
        carpool.setMember(member);
        carpool.setDepartureCoordinate(carpoolRequestDTO.getDepartureCoordinate());
        carpool.setDepartureDetailed(carpoolRequestDTO.getDepartureDetailed());
        carpool.setDepartureDateTime(carpoolRequestDTO.getDepartureTime());
        carpool.setChatLink(carpoolRequestDTO.getChatLink());
        carpool.setCapacity(carpoolRequestDTO.getCapacity());
        carpool.setCost(carpoolRequestDTO.getCost());

        carpoolRepository.save(carpool);
        //save를 먼저 했기 때문에 carpool.getId가 가능
        member.setReservation(true);
        member.setCarpoolId(carpool.getId());

        memberRepository.save(member);

        return ResponseEntity.ok("카풀 등록 성공했습니다.");
    }



    //카플 예약
    //여기서 바로 내가 예약한 화면으로 넘어가기 때문에 passngerInfo 넘겨야 하지 않나요
    @Transactional
    public ResponseEntity<String> reservationCarpool(CustomUserDetails userDetails, ReservationCarpoolRequestDTO requestDTO) {

        Member member = userDetails.getMember();

        //중복예약 검증
        if(member.getReservation()){
            throw new CustomException(ErrorCode.CARPOOL_ALREADY_RESERVATIONS);
        }

        //카풀 테이블에 있는 해당 카풀 정보 다 가져옴
        CarpoolEntity carpool = findByCarpool(requestDTO.getCarpoolId());

        //좌석 수 검증
        if (carpool.getCapacity() < carpool.getReservationCount()) {
           throw new CustomException(ErrorCode.CARPOOL_IS_FULL);
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCarpool(carpool);
        reservation.setMember(member);

        reservationRepository.save(reservation);

        member.setReservation(true);
        member.setCarpoolId(requestDTO.getCarpoolId());
        memberRepository.save(member);

        carpool.incrementReservationCount();

        return ResponseEntity.ok("예약을 성공했습니다.");
    }

    //카풀 취소
    @Transactional
    public ResponseEntity<String> cancelCarpool(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        Long carpoolId = member.getCarpoolId();

        CarpoolEntity carpool = findByCarpool(carpoolId);

        ReservationEntity reservation = reservationRepository.findByCarpoolAndMember(carpoolId, member.getId());

        reservationRepository.delete(reservation);

        carpool.decrementReservationCount();
        //영속성 컨텍스트에서 자동반영 됨
        carpoolRepository.save(carpool);

        member.setCarpoolId(null);
        member.setReservation(false);
        memberRepository.save(member);

        return ResponseEntity.ok("카풀 예약이 성공적으로 취소 되었습니다.");
    }

    //카풀 삭제
    public ResponseEntity<String> deleteCarpool(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        Long carpoolId = member.getCarpoolId();

        CarpoolEntity carpool = findByCarpool(carpoolId);
        //삭제전에 멤버 다 찾아서

        //드라이버 정보 삭제
        member.setCarpoolId(null);
        member.setReservation(false);
        memberRepository.save(member);

        //승객 모두 찾아서 멤버 데이터 수정
        List<ReservationEntity> reservationEntities = reservationRepository.findByCarpool(carpoolId);

        for (ReservationEntity r : reservationEntities) {
            Member ReservationMember = memberRepository.findById(r.getMember().getId())
                            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
            ReservationMember.setCarpoolId(null);
            ReservationMember.setReservation(false);
        }

        reservationRepository.deleteCarpool(carpoolId);

        carpoolRepository.delete(carpool);

        return ResponseEntity.ok("카풀이 정상적으로 삭제 되었습니다.");
    }



    // 카풀 삭제하여 로그로 남기기

    //탑승한 카풀 조회

    //초기화 하는 코드

    private CarpoolEntity findByCarpool(Long carpoolId){
        return carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new CustomException(ErrorCode.CARPOOL_NOT_FOUND));
    }
}


// webServer <nginx , apache 어떻게 request 를 분산할것인지전략 로드밸런싱.
// 서버에서 데이터처리를 어떻게 효율적으로할것인지.
// Lock을 공부해고 수업을 듣기
// 키워드: transactional ACID (솔리드 법칙도 추가로)
// I = isolatation <<<동시성
// read_lock ***
// write_lock ***
// database 마다 isolation 이 다름 / 기본형isolation >> 어떤형태(1,2,3,4)
// 샤딩>> 100석의 예약 서버를 5대 분산 1~20/1   21~40/2번
// read > 서버
// write >서버
// 인증서버 따로
// write = read re....그걸받아올수있는 방식도 공부..
// 메모리가빠르잖아 > 레디스 ㄱㄱ 레디스> 단일쓰레드 메모리의단점 .. 누수..메모리 누수
// 어셈블리, 가상메모리 , 커널, blocking , 네트워크 , 컨텍스트스위칭, 쓰레드, 프로세스, 자료구조, 10 3~4 5레벨도
