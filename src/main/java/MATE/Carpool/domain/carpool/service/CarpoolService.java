package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.common.PKEncryption;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
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

        List<CarpoolResponseDTO> carpoolList =
                carpoolRepository.findByAllList(blockStart)
                        .stream()
                        .map(CarpoolResponseDTO::new)
                        .toList();

        return ResponseEntity.ok(carpoolList);

    }

    /*질문 내용
    * 1. 커스텀 오류
    * 2. 언제 .orElseThrow(()를 해야하는지
    * */
    //내가 등록한 카풀 조회
    @Transactional
    public ResponseEntity<List<PassengerInfoDTO>> myCarpool( Long carpoolId) throws Exception {

        // 로그인한 사용자의 ID
        //String memberId = user.getMemberId(); 가 안됨 CustomUserDetails에 override가 안되어 있음

        // 해당 카풀 엔티티 조회
        CarpoolEntity carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카풀이 존재하지 않습니다."));

        List<ReservationEntity> reservations = reservationRepository.findByCarpool(carpool);

        // 예약 정보를 PassengerInfoDTO 리스트로 변환
        List<PassengerInfoDTO> passengerInfoList = reservations.stream()
                .map(reservation -> new PassengerInfoDTO(reservation.getMember()))
                .toList();

        return ResponseEntity.ok(passengerInfoList);

    }


    //내가 신청한 카풀 조회
    /**
     *
     * 프론트에서 암호화된 문자열을 보냄
     * 복호화> memberId=LongPk를 찾아요 1
     * reservationRepository > findByMemberId   [memberId : 1 carPoolId : 11]
     * first조회 <약간 안써야한다고 하는사람좀있음>
     * 그동안신청했던 목록도 나오게됨
     * carPoolRepo > findById (11)
     *
     */

    //카풀 생성

    //카플 예약
//    @Transactional(readOnly = true)
//    public MyCarpoolResponseDTO getMyCarpool(Long memberId) {
//
//        // 기준 시간 계산
//        LocalDateTime blockStart = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
//        if (LocalDateTime.now().isBefore(blockStart)) {
//            blockStart = blockStart.minusDays(1);
//        }
//        LocalDateTime blockEnd = blockStart.plusDays(1);
//
//        // 현재 사용자(memberId)의 예약 중 해당 시간 블록 내 카풀 정보 조회
//        List<ReservationEntity> reservations = reservationRepository.findReservationsByMemberIdAndTimeBlock(memberId, blockStart, blockEnd);
//    }


        //카풀 취소

    //카풀 삭제
    // 카풀 삭제하여 로그로 남기기

    //탑승한 카풀 조회
}
