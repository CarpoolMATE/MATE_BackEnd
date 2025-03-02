package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.common.Message;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.request.ReservationCarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolHistoryResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolWithPassengersDTO;
import MATE.Carpool.domain.carpool.dto.response.PassengerInfoDTO;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.carpool.repository.ReservationRepository;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static MATE.Carpool.domain.carpool.service.CarpoolRequestDtoGenerator.generateRandomCarpoolRequestDTO;

@Service
@Slf4j
@RequiredArgsConstructor //자동으로 생성자 작성
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    //최신순으로 카풀 정보 리스트로 받기
    @Transactional(readOnly = true)
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> getCarpoolList(){

        LocalDateTime blockStart = getBlockStart();

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByAllList(blockStart);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(new Message<>("카풀 조회 성공", HttpStatus.OK,carpoolResponseDTOS));

    }


    //TODO: 현재 모집중인 카풀 리스트만 조회
    @Transactional
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> onlyActiveCarpoolList(){

        LocalDateTime blockStart = getBlockStart();

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByActiveList(blockStart);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(new Message<>("모집중인 카풀만 조화",HttpStatus.OK,carpoolResponseDTOS));
    }

    //TODO: 빠른 시간순으로 카풀 리스트 조회
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> fastCarpoolList(){

        LocalDateTime blockStart = getBlockStart();

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByFastList(blockStart);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(new Message<>("빠른 시간순 카풀 조회",HttpStatus.OK,carpoolResponseDTOS));
    }

    //TODO: 가격 낮은 순으로 카풀 리스트 조회
    public ResponseEntity<Message<List<CarpoolResponseDTO>>> lowCostCarpoolList(){

        LocalDateTime blockStart = getBlockStart();

        List<CarpoolEntity> carpoolEntityList = carpoolRepository.findByLowCostList(blockStart);

        List<CarpoolResponseDTO> carpoolResponseDTOS = new ArrayList<>();

        for (CarpoolEntity c : carpoolEntityList) {
            carpoolResponseDTOS.add(new CarpoolResponseDTO(c));
        }

        return ResponseEntity.ok(new Message<>("낮은 가격순 카풀 조회",HttpStatus.OK,carpoolResponseDTOS));
    }

    //진행중인 카풀
    @Transactional
    public ResponseEntity<Message<CarpoolWithPassengersDTO>> myCarpool(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        if (member.getCarpoolId() == null) {
            throw new CustomException(ErrorCode.CARPOOL_NOT_FOUND);  // 또는 적절한 예외 처리
        }

        // 해당 카풀 엔티티 조회
        CarpoolEntity carpool = findByCarpool(member.getCarpoolId());

        CarpoolResponseDTO carpoolResponseDTO = new CarpoolResponseDTO(carpool);

        List<PassengerInfoDTO> passengerInfoDTOS = getPassengerInfo(carpool);

        boolean isDriver = carpool.getMember().getMemberId().equals(userDetails.getMember().getMemberId());

        CarpoolWithPassengersDTO responseDTO = new CarpoolWithPassengersDTO(carpoolResponseDTO, passengerInfoDTOS,isDriver);

        return ResponseEntity.ok(new Message<>("내 카풀 조회",HttpStatus.OK, responseDTO));

    }

    //카풀 생성
    //카풀 생성시간 오전 7:00에서 9:00으로 고정
    @Transactional
    public ResponseEntity<Message<CarpoolResponseDTO>> makeCarpool(CustomUserDetails userDetails, CarpoolRequestDTO carpoolRequestDTO) {

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

        carpoolRepository.save(carpool);
        //save를 먼저 했기 때문에 carpool.getId가 가능

        member.setReservation(true);
        member.incrementCarpoolCount();
        member.setCarpoolId(carpool.getId());

        memberRepository.save(member);
        CarpoolResponseDTO response = new CarpoolResponseDTO(carpool);

        return ResponseEntity.ok(new Message<>("카풀 생성 성공",HttpStatus.OK,response));
    }

    //카플 예약
    @Transactional
    public ResponseEntity<Message<Long>> reservationCarpool(CustomUserDetails userDetails,ReservationCarpoolRequestDTO requestDTO) {

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

        Long id = carpool.getId();

        return ResponseEntity.ok(new Message<>("카풀 예약 성공",HttpStatus.OK, id));
    }

    //카풀 취소
    @Transactional
    public ResponseEntity<Message<String>> cancelCarpool(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        Long carpoolId = member.getCarpoolId();

        if (member.getCarpoolId() == null) {
            throw new CustomException(ErrorCode.CARPOOL_NOT_FOUND);
        }



        CarpoolEntity carpool = carpoolRepository.findById(carpoolId).orElse(null);

        ReservationEntity reservation = reservationRepository.findByCarpoolAndMember(carpoolId, member.getId());

        if(carpool != null) {
            carpoolRepository.save(carpool);
            carpool.decrementReservationCount();
        }

        reservationRepository.delete(reservation);

        member.setCarpoolId(null);
        member.setReservation(false);
        member.decrementCarpoolCount();
        memberRepository.save(member);

        return ResponseEntity.ok(new Message<>("카풀 취소 성공",HttpStatus.OK,"성공"));
    }

    //카풀 삭제
    @Transactional
    public ResponseEntity<Message<String>> deleteCarpool(CustomUserDetails userDetails) {

        Member member = userDetails.getMember();

        if (member.getCarpoolId() == null) {
            throw new CustomException(ErrorCode.CARPOOL_NOT_FOUND);
        }

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


        return ResponseEntity.ok(new Message<>("카풀 삭제 성공",HttpStatus.OK,"성공"));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message<List<CarpoolHistoryResponseDTO>>> getCarpoolHistory(CustomUserDetails userDetails) {

        LocalDateTime blockStart = getBlockStart();

        Member member = userDetails.getMember();

        List<CarpoolHistoryResponseDTO> result = new ArrayList<>();

        //예약에서 목록 가져오기
        List<ReservationEntity> reservationEntities = reservationRepository.findByRideCarpoolHis(member, blockStart);
        for (ReservationEntity r : reservationEntities) {
            result.add(new CarpoolHistoryResponseDTO(r.getCarpool()));
        }

        return ResponseEntity.ok(new Message<>("탑승 목록 조회 성공",HttpStatus.OK,result));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message<List<CarpoolHistoryResponseDTO>>> getDriverHistory(CustomUserDetails userDetails) {

        LocalDateTime blockStart = getBlockStart();

        Member member = userDetails.getMember();

        List<CarpoolHistoryResponseDTO> result = new ArrayList<>();

        if (member.getIsDriver()) {
            List<CarpoolEntity> driverCarpool = carpoolRepository.findByDriverHis(member, blockStart);
            for (CarpoolEntity c : driverCarpool) {
                result.add(new CarpoolHistoryResponseDTO(c));
            }
        } else {
            throw new CustomException(ErrorCode.DRIVER_NOT_FOUND);
        }

        return ResponseEntity.ok(new Message<>("운행 목록 조회 성공",HttpStatus.OK,result));
    }

    // 매일 오전 10시에 실행되는 메서드
    @Scheduled(cron = "0 0 10 * * ?") // cron 표현식으로 매일 오전 10시 설정
    public void resetMemberReservationsAndCarpoolId() {
        memberRepository.updateReservationAndCarpoolId();
    }

    public static LocalDateTime getBlockStart() {
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

