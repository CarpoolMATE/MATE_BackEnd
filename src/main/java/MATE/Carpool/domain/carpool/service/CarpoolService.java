package MATE.Carpool.domain.carpool.service;

import MATE.Carpool.domain.carpool.dto.request.CarpoolRequestDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseListDTO;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor //자동으로 생성자 작성
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;

    //목록 조회 (홈) => 오전 10시 기준으로 조회
    public ResponseEntity<CarpoolResponseListDTO> GetCarpoolList(){

        // 1. 오전 10시인지 체크
        // 오전 10시를 기준으로 오전 10시 전에 생성된 카풀을 조회
        // 출발지, 가격, 출발 시간, 드라이버 차량 사진, 현재 탑승 인원 수, 탑승 최대 인원 수 이 정보를 가져오기
        // 뿌려주기 리스트로
        // 페이징

        return null;
    }

    //카풀 상세 조회

    //내 카풀 조회

    //카풀 생성

    //카플 예약

    //카풀 취소

    //카풀 삭제

    //탑승한 카풀 조회
}
