package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor //자동으로 생성자 작성
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final PKEncryption pkEncryption;

    //목록 조회 (홈) => 오전 10시 기준으로 조회
    @Transactional(readOnly = true)
    public ResponseEntity<List<CarpoolResponseDTO>> GetCarpoolList(){

        // 오늘 오전 10시 기준
        LocalDateTime blockStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(10,0));
        if (LocalDateTime.now().isBefore(blockStart)) {
            blockStart = blockStart.minusDays(1);
        }


        return ResponseEntity.ok(null);

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

    private CarpoolEntity findByCarpool(Long carpoolId){
      return carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new IllegalArgumentException("  "));
    }

    //카풀 생성

    //카플 예약
    public ResponseEntity<String> reservationCarpool(ReservationCarpoolRequestDTO requestDTO) {

        //Reservation 테이블에 있는 해당 카풀 정보 다 가져옴
        List<ReservationEntity> reservationEntities = reservationRepository.findByCarpool(requestDTO.getCarpoolId());

        //해당 카풀의 capacity 확인
        CarpoolEntity carpool = carpoolRepository.findById(requestDTO.getCarpoolId());
        int capacity = carpool.getCapacity();
        int reservationCount = carpool.getReservationCount();
        if (capacity > reservationCount ) {

        }
        //예약을 완료하면 reservationCount +1
        //그리고 멤버에서 예약 번호 정하기

        //추가로 락
    }

    //카풀 취소

    //카풀 삭제

    // 카풀 삭제하여 로그로 남기기

    //탑승한 카풀 조회

    //초기화 하는 코드
}
