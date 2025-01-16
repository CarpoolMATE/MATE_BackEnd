package MATE.Carpool.domain.admin.service;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.carpool.service.CarpoolService;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import MATE.Carpool.domain.report.dto.ReportResponseDto;
import MATE.Carpool.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final CarpoolRepository carpoolRepository;
    private final ReportRepository reportRepository;

    //회원전체조회
    @Transactional(readOnly = true)
    public ResponseEntity<List<MemberResponseDto>> readAll() {
        List<MemberResponseDto> memberResponseDtoList = memberRepository.findAll().stream()
                .map(MemberResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(memberResponseDtoList);
    }

    @Transactional
    public ResponseEntity<String> isBanned(CustomUserDetails userDetails, String memberId) {
        return memberRepository.findByMemberId(memberId)
                .map(member -> {
                    if (member.getId().equals(userDetails.getMember().getId())) {
                        throw new CustomException(ErrorCode.CAN_NOT_BAN_ONESELF);
                    }
                    member.setIsBanned(!member.getIsBanned());
                    return member;
                })
                .map(member -> {
                    String statusMessage = member.getIsBanned()
                            ? String.format("ID : %s 회원이 정지 되었습니다.", member.getMemberId())
                            : String.format("ID : %s 회원이 정지해제 되었습니다.", member.getMemberId());
                    return ResponseEntity.ok(statusMessage);
                })
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public ResponseEntity<CarpoolResponseDTO> readOne(Long id) {
        return   carpoolRepository.findById(id)
                .map(CarpoolResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElseThrow(()->new CustomException(ErrorCode.CARPOOL_NOT_FOUND));
    }


    //클라이언트 사이드
    public ResponseEntity<List<CarpoolResponseDTO>> readAllCarpool() {
        return ResponseEntity.ok(carpoolRepository.findAll().stream()
                .map(CarpoolResponseDTO::new)
                .collect(Collectors.toList()));
    }

    //서버사이드 페이징  ex) carpools?page=2&size=5
    public ResponseEntity<List<CarpoolResponseDTO>> readAllCarpool(Pageable pageable) {

        List<CarpoolResponseDTO> responseDTOS= carpoolRepository.findAll(pageable).stream()
                .map(CarpoolResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOS);

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

    //신고전체 조회
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReportResponseDto>> reportFindAll(){
        return ResponseEntity.ok(
                reportRepository.findAllReports().stream()
                        .map(ReportResponseDto::new)
                        .toList()
        );
    }

    @Transactional
    public ResponseEntity<List<ReportResponseDto>> readAllByCarpool(Long id) {
        return ResponseEntity.ok(
                reportRepository.findByCarpoolId(id)
                        .stream()
                        .map(ReportResponseDto::new)
                        .collect(Collectors.toList()));
    }



    //신고내역 조회
}
