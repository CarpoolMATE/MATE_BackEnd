package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor //자동으로 생성자 작성
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    //목록 조회 (홈) => 오전 10시 기준으로 조회
    public ResponseEntity<CarpoolResponseListDTO> GetCarpoolList(){

        // 카풀 조회 기준 날짜 생성
        LocalDateTime blockStart = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        if (LocalDateTime.now().isBefore(blockStart)) {
            blockStart = blockStart.minusDays(1);
        }

        List<CarpoolEntity> carpoolEntity = carpoolRepository.

        return null;
    }

    //카풀 상세 조회

    //내 카풀 조회

    //카풀 생성

    //카플 예약
    @Transactional(readOnly = true)
    public MyCarpoolResponseDTO getMyCarpool(Long memberId) {

        // 기준 시간 계산
        LocalDateTime blockStart = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        if (LocalDateTime.now().isBefore(blockStart)) {
            blockStart = blockStart.minusDays(1);
        }
        LocalDateTime blockEnd = blockStart.plusDays(1);

        // 현재 사용자(memberId)의 예약 중 해당 시간 블록 내 카풀 정보 조회
        List<ReservationEntity> reservations = reservationRepository.findReservationsByMemberIdAndTimeBlock(memberId, blockStart, blockEnd);
    }


        //카풀 취소

    //카풀 삭제

    //탑승한 카풀 조회
}
