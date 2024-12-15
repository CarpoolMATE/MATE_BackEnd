package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseListDTO;
import MATE.Carpool.domain.carpool.dto.response.MyCarpoolResponseDTO;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.carpool.repository.ReservationRepository;
import MATE.Carpool.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    //목록 조회 (홈) => 오전 10시 기준으로 조회


    @Transactional(readOnly = true)
    public ResponseEntity<List<CarpoolResponseListDTO>> GetCarpoolList(){

        // 오늘 오전 10시 기준
        LocalDateTime blockStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(10,0));
        if (LocalDateTime.now().isBefore(blockStart)) {
            blockStart = blockStart.minusDays(1);
        }

        List<CarpoolResponseListDTO> carpoolList =
                carpoolRepository.findByAllList(blockStart)
                        .stream()
                        .map(CarpoolResponseListDTO::new)
                        .toList();

        return ResponseEntity.ok(carpoolList);

    }

    //카풀 상세 조회

    //내가 등록한 카풀 조회
    /**
     * 프론트에서 암호화된 문자열을 보냄
     * 복호화> memberId=LongPk를 찾아요
     * carpoolrepository > findByMemberId (memberId) 맨처음찾는것.
     */

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

    //탑승한 카풀 조회
}
