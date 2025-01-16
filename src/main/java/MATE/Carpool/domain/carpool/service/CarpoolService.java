package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.request.ReservationCarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolHistoryResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.PassengerInfoDTO;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.carpool.repository.ReservationRepository;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor //자동으로 생성자 작성
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final EntityManager em;

    //최신순으로 카풀 정보 리스트로 받기
    @Transactional(readOnly = true)
    public ResponseEntity<List<CarpoolResponseDTO>> getCarpoolList(CustomUserDetails userDetails){

        String university = getUniversity(userDetails);

        LocalDateTime blockStart = getBlockStart();

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByAllList(blockStart, university);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(carpoolResponseDTOS);

    }



    //TODO: 현재 모집중인 카풀 리스트만 조회
    @Transactional
    public ResponseEntity<List<CarpoolResponseDTO>> onlyActiveCarpoolList(CustomUserDetails userDetails){

        String university = getUniversity(userDetails);

        LocalDateTime blockStart = getBlockStart();

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByActiveList(blockStart, university);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(carpoolResponseDTOS);
    }

    //TODO: 빠른 시간순으로 카풀 리스트 조회
    public ResponseEntity<List<CarpoolResponseDTO>> fastCarpoolList(CustomUserDetails userDetails){

        String university = getUniversity(userDetails);

        LocalDateTime blockStart = getBlockStart();

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByFastList(blockStart, university);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(carpoolResponseDTOS);
    }

    //TODO: 가격 낮은 순으로 카풀 리스트 조회
    public ResponseEntity<List<CarpoolResponseDTO>> lowCostCarpoolList(CustomUserDetails userDetails){

        String university = getUniversity(userDetails);

        LocalDateTime blockStart = getBlockStart();

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByLowCostList(blockStart, university);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(carpoolResponseDTOS);
    }

    //진행중인 카풀
    @Transactional
    public ResponseEntity<List<PassengerInfoDTO>> myCarpool(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        // 해당 카풀 엔티티 조회
        CarpoolEntity carpool = findByCarpool(member.getCarpoolId());

        List<PassengerInfoDTO> passengerInfoDTOS = getPassengerInfo(carpool);

        return ResponseEntity.ok(passengerInfoDTOS);
    }

    //카풀 생성
    //카풀 생성시간 오전 7:00에서 9:00으로 고정
    @Transactional
    public ResponseEntity<CarpoolResponseDTO> makeCarpool(CustomUserDetails userDetails,CarpoolRequestDTO carpoolRequestDTO) {


        Member member = userDetails.getMember();

        if(!member.getIsDriver()){
            throw new CustomException(ErrorCode.ONLY_POSSIBLE_DRIVER);
        }
        if(member.getReservation()){
            throw new CustomException(ErrorCode.ALREADY_CARPOOL_EXIST);
        }

        CarpoolEntity carpool = new CarpoolEntity();
        carpool.setMember(member);
        carpool.setDepartureCoordinate(carpoolRequestDTO.getDepartureCoordinate());
        carpool.setDepartureDateTime(carpoolRequestDTO.getDepartureTime());
        carpool.setChatLink(carpoolRequestDTO.getChatLink());
        carpool.setCapacity(carpoolRequestDTO.getCapacity());
        carpool.setCost(carpoolRequestDTO.getCost());
        carpool.setUniversity(member.getUniversity());

        carpoolRepository.save(carpool);
        //save를 먼저 했기 때문에 carpool.getId가 가능

        member.setReservation(true);
        member.incrementCarpoolCount();
        member.setCarpoolId(carpool.getId());

        memberRepository.save(member);

        return ResponseEntity.ok(new CarpoolResponseDTO(carpool));
    }

    //카플 예약
    @Transactional
    public ResponseEntity<List<PassengerInfoDTO>> reservationCarpool(CustomUserDetails userDetails, ReservationCarpoolRequestDTO requestDTO) {

        Member member = userDetails.getMember();

        //카풀 테이블에 있는 해당 카풀 정보 다 가져옴
        CarpoolEntity carpool = findByCarpool(requestDTO.getCarpoolId());

        //중복예약 검증
        if(member.getReservation()){
            if(member.getId().equals(carpool.getMember().getId())){
                throw new CustomException(ErrorCode.CAN_NOT_RESERVATION_MY_CARPOOL);
            }
            throw new CustomException(ErrorCode.CARPOOL_ALREADY_RESERVATIONS);
        }

        //좌석 수 검증
        if (carpool.getCapacity() <= carpool.getReservationCount()) {
           throw new CustomException(ErrorCode.CARPOOL_IS_FULL);
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCarpool(carpool);
        reservation.setMember(member);

        reservationRepository.save(reservation);

        member.setReservation(true);
        member.incrementCarpoolCount();
        member.setCarpoolId(requestDTO.getCarpoolId());
        memberRepository.save(member);

        carpool.incrementReservationCount();

        List<PassengerInfoDTO> passengerInfoDTOS = getPassengerInfo(carpool);

        return ResponseEntity.ok(passengerInfoDTOS);
    }

    //카풀 취소
    //TODO: 드라이버 패신저를 프론트에서 어떻게 알 수 있는가
    @Transactional
    public ResponseEntity<String> cancelCarpool(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        Long carpoolId = member.getCarpoolId();

        CarpoolEntity carpool = carpoolRepository.findById(carpoolId).orElse(null);

        ReservationEntity reservation = reservationRepository.findByCarpoolAndMember(carpoolId, member.getId());

        if(carpool != null){
          carpoolRepository.save(carpool);
          carpool.decrementReservationCount();
        }
        reservationRepository.delete(reservation);

        member.setCarpoolId(null);
        member.setReservation(false);
        member.decrementCarpoolCount();
        memberRepository.save(member);

        return ResponseEntity.ok("카풀 예약이 성공적으로 취소 되었습니다.");
    }

    //카풀 삭제
    @Transactional
    public ResponseEntity<String> deleteCarpool(CustomUserDetails userDetails) {
        Member member = userDetails.getMember();

        CarpoolEntity carpool = carpoolRepository.findById(member.getCarpoolId()).orElseThrow(
                () -> new CustomException(ErrorCode.CARPOOL_NOT_FOUND)
        );

        List<Member> members =new ArrayList<>();

        if(!carpool.getMember().getId().equals(member.getId())){
            throw new CustomException(ErrorCode.ONLY_DELETE_DRIVER);
        }

        List<ReservationEntity> r = reservationRepository.findByCarpoolId(carpool.getId());
        for( ReservationEntity reservation : r){
            reservation.setCarpoolNull();
            members.add(reservation.getMember());
        }

        members.add(member);

        for (Member m : members){
            m.setReservation(false);
            m.setCarpoolId(null);
            m.decrementCarpoolCount();
        }

        memberRepository.saveAll(members);
        reservationRepository.deleteAll(r);
        carpoolRepository.delete(carpool);


        return ResponseEntity.ok("카풀이 정상적으로 삭제 되었습니다.");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<CarpoolHistoryResponseDTO>> getCarpoolHistory(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        List<CarpoolHistoryResponseDTO> result = new ArrayList<>();

        //예약에서 목록 가져오기
        List<ReservationEntity> reservationEntities = reservationRepository.findByCarpoolHis(member);
        for (ReservationEntity r : reservationEntities) {
            result.add(new CarpoolHistoryResponseDTO(r.getCarpool()));
        }

        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<CarpoolHistoryResponseDTO>> getDriverHistory(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        List<CarpoolHistoryResponseDTO> result = new ArrayList<>();

        if (member.getIsDriver()) {
            List<CarpoolEntity> driverCarpool = carpoolRepository.findByMemberHis(member);
            for (CarpoolEntity c : driverCarpool) {
                result.add(new CarpoolHistoryResponseDTO(c));
            }
        } else {
            throw new CustomException(ErrorCode.DRIVER_NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    // 매일 오전 10시에 실행되는 메서드
    @Scheduled(cron = "0 0 10 * * ?") // cron 표현식으로 매일 오전 10시 설정
    public void resetMemberReservationsAndCarpoolId() {
        memberRepository.updateReservationAndCarpoolId();
    }


    private static String getUniversity(CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        String university = member.getUniversity();
        return university;
    }

    private static LocalDateTime getBlockStart() {
        LocalDateTime blockStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(10,0));
        if (LocalDateTime.now().isBefore(blockStart)) {
            blockStart = blockStart.minusDays(1);
        }
        return blockStart;
    }


    private CarpoolEntity findByCarpool(Long carpoolId){
        return carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new CustomException(ErrorCode.CARPOOL_NOT_FOUND));
    }

    private List<PassengerInfoDTO> getPassengerInfo(CarpoolEntity carpool) {
        List<ReservationEntity> reservationEntities = reservationRepository.findByCarpoolId(carpool.getId());

        List<PassengerInfoDTO> passengerInfoDTOS = new ArrayList<>();

        for (ReservationEntity r : reservationEntities) {
            passengerInfoDTOS.add(new PassengerInfoDTO(r.getMember()));
        }
        return passengerInfoDTOS;
    }


}

