package MATE.Carpool.domain.admin.service;


import MATE.Carpool.common.Message;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.admin.dto.CarpoolResponseResultDTO;
import MATE.Carpool.domain.admin.dto.MemberResponseDTO;
import MATE.Carpool.domain.admin.dto.MemberResponseResultDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import MATE.Carpool.domain.report.dto.ReportResponseDto;
import MATE.Carpool.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public ResponseEntity<Message<MemberResponseResultDTO>> readAllMembers(int size, int page) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
        Page<MemberResponseDTO> memberPage = memberRepository.findAllMemberPagination(pageable);

        return ResponseEntity.ok(new Message<>("전체 회원 조회 성공", HttpStatus.OK,new MemberResponseResultDTO(
                        memberPage.getContent(),
                        memberPage.getTotalElements(),
                        memberPage.getTotalPages()
                )));
    }

    //회원전체조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message<MemberResponseResultDTO>> readAllDrivers(int size, int page) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
        Page<MemberResponseDTO> memberPage = memberRepository.findAllDriverPagination(pageable);

        return ResponseEntity.ok(new Message<>("전체 드라이버 조회 성공", HttpStatus.OK,new MemberResponseResultDTO(
                memberPage.getContent(),
                memberPage.getTotalElements(),
                memberPage.getTotalPages()
        )));

    }


    @Transactional
    public ResponseEntity<Message<Boolean>> isBanned(CustomUserDetails userDetails, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (member.getId().equals(userDetails.getMember().getId())) {
            throw new CustomException(ErrorCode.CAN_NOT_BAN_ONESELF);
        }

        member.setIsBanned(!member.getIsBanned());

        return ResponseEntity.ok(new Message<>("회원 정지 처리 완료", HttpStatus.OK, member.getIsBanned()));
    }


    public ResponseEntity<Message<CarpoolResponseDTO>> readOne(Long id) {
        return ResponseEntity.ok(
                new Message<>("카풀 단일 조회 성공",HttpStatus.OK,
                        carpoolRepository.findById(id)
                                .map(CarpoolResponseDTO::new)
                                .orElseThrow(()->new CustomException(ErrorCode.CARPOOL_NOT_FOUND))
                        ));

    }


    //서버사이드 페이징  ex) carpools?page=2&size=5
    public ResponseEntity<Message<CarpoolResponseResultDTO>> readAllCarpool(int size, int page, LocalDateTime startDate, LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Order.desc("createdAt")));

        Page<CarpoolResponseDTO> responseDTOS= carpoolRepository.findByCarpoolToPeriod(pageable,startDate,endDate);
        List<CarpoolResponseDTO> carpools = responseDTOS.getContent();
        Long totalCount = responseDTOS.getTotalElements();
        int totalPage = responseDTOS.getTotalPages();

        return ResponseEntity.ok(new Message<>("카풀 목록 조회 성공",HttpStatus.OK,new CarpoolResponseResultDTO(
                carpools,totalCount,totalPage
        )));

    }

    //신고 상세조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message<ReportResponseDto>> reportFindById(Long id){
        return ResponseEntity.ok(new Message<>("신고 상세조회 성공",HttpStatus.OK,
                reportRepository.findReportById(id)
                        .map(ReportResponseDto::new)
                        .orElseThrow(()->new CustomException(ErrorCode.REPORT_NOT_FOUND))
                        )
        );
    }

    //신고전체 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message<List<ReportResponseDto>>> reportFindAll(){
        return ResponseEntity.ok(new Message<>("신고목록 전체조회 성공",HttpStatus.OK,
                reportRepository.findAllReports().stream()
                .map(ReportResponseDto::new)
                .toList())
        );
    }

    @Transactional
    public ResponseEntity<Message<List<ReportResponseDto>>> readAllByCarpool(Long id) {
        return ResponseEntity.ok(
                new Message<>("신고목록 조회 완료",HttpStatus.OK,
                reportRepository.findByCarpoolId(id)
                        .stream()
                        .map(ReportResponseDto::new)
                        .toList()
        ));
    }



    //신고내역 조회
}
