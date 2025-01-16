package MATE.Carpool.domain.report.service;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.carpool.repository.ReservationRepository;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.report.dto.ReportRequestDto;
import MATE.Carpool.domain.report.dto.ReportResponseDto;
import MATE.Carpool.domain.report.entity.ReportEntity;
import MATE.Carpool.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final CarpoolRepository carpoolRepository;
    private final ReservationRepository reservationRepository;

    //신고전체 조회
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReportResponseDto>> reportFindAll(){
        return ResponseEntity.ok(
                reportRepository.findAllReports().stream()
                        .map(ReportResponseDto::new)
                        .toList()
        );
    }
    //신고 상세조회
    @Transactional(readOnly = true)
    public ResponseEntity<ReportResponseDto> reportFindById(Long id){
        return ResponseEntity.ok(
                reportRepository.findReportById(id)
                        .map(ReportResponseDto::new)
                        .orElseThrow(()->new CustomException(ErrorCode.REPORT_NOT_FOUND))
        );
    }

    
    //신고하기
    @Transactional
    public ResponseEntity<String> submitReport(ReportRequestDto requestDto, Long carpoolId, CustomUserDetails userDetails) {

        //이용한 카풀에만 신고할 수 있도록

        CarpoolEntity carpoolEntity = carpoolRepository.findById(carpoolId)
                .orElseThrow(()->new CustomException(ErrorCode.CARPOOL_NOT_FOUND));

        Long memberId = userDetails.getMember().getId();


        List<Long> memberIds =reservationRepository.findByMemberId(carpoolEntity.getId());

        if(carpoolEntity.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {
            throw new CustomException(ErrorCode.CARPOOL_REPORT_NOT_ALLOWED);
        }

        if(carpoolEntity.getMember().getId().equals(memberId)){
            throw new CustomException(ErrorCode.CAN_NOT_REPORT_MY_CARPOOL);
        }

        boolean isMemberInCarpool = memberIds.contains(memberId);
        if (!isMemberInCarpool) {
            throw new CustomException(ErrorCode.CAN_NOT_REPORT_NOT_USE_CARPOOL);
        }


        Member member = userDetails.getMember();

        ReportEntity reportEntity = ReportEntity.builder()
                .carpool(carpoolEntity)
                .member(member)
                .reportTitle(requestDto.getReportTitle())
                .reportContent(requestDto.getReportContent())
                .build();

        reportRepository.save(reportEntity);

        return ResponseEntity.ok("신고 접수가 완료되었습니다.");

    }

    public ResponseEntity<ReportResponseDto> readOne(Long id) {
        return reportRepository.findById(id)
                .map(ReportResponseDto::new)
                .map(ResponseEntity::ok)
                .orElseThrow(()->new CustomException(ErrorCode.REPORT_NOT_FOUND));
    }

    public ResponseEntity<List<ReportResponseDto>> readAllByCarpool(Long id) {
        return ResponseEntity.ok(
                reportRepository.findByCarpoolId(id)
                        .stream()
                        .map(ReportResponseDto::new)
                        .collect(Collectors.toList()));
    }
}
