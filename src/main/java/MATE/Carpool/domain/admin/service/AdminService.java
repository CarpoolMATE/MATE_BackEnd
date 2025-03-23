package MATE.Carpool.domain.admin.service;


import MATE.Carpool.common.Message;
import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.config.jwt.JwtProvider;
import MATE.Carpool.config.userDetails.CustomUserDetails;
import MATE.Carpool.domain.admin.dto.CarpoolResponseResultDTO;
import MATE.Carpool.domain.admin.dto.MemberResponseDTO;
import MATE.Carpool.domain.admin.dto.PageResponseResultDTO;
import MATE.Carpool.domain.carpool.dto.response.AdminCarpoolInfoDTO;
import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.dto.response.PassengerInfoDTO;
import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.carpool.repository.CarpoolRepository;
import MATE.Carpool.domain.carpool.repository.ReservationRepository;
import MATE.Carpool.domain.member.dto.request.SignInRequestDto;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.entity.MemberType;
import MATE.Carpool.domain.member.repository.MemberRepository;
import MATE.Carpool.domain.report.dto.ReportResponseDto;
import MATE.Carpool.domain.report.entity.ReportEntity;
import MATE.Carpool.domain.report.repository.ReportRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final CarpoolRepository carpoolRepository;
    private final ReportRepository reportRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ResponseEntity<Message<Object>> signIn(SignInRequestDto requestDto, HttpServletResponse response, HttpServletRequest request)  {
        String memberId = requestDto.getMemberId();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberId, requestDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

//        jwtProvider.createTokenAndSavedTokenHttponly(authentication, response,request, memberId);
        jwtProvider.createTokenAndSaved(authentication, response,request);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Member member = customUserDetails.getMember();
        if(!member.getMemberType().equals(MemberType.ADMIN)){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        MemberResponseDto memberResponseDto = new MemberResponseDto(member);

        return ResponseEntity.ok(new Message<>("로그인",HttpStatus.OK,memberResponseDto));
    }

    //회원전체조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message<PageResponseResultDTO<MemberResponseDTO>>> readAllMembers(int size, int page) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("id")));
        Page<MemberResponseDTO> memberPage = memberRepository.findAllMemberPagination(pageable);

        return ResponseEntity.ok(new Message<>("전체 회원 조회 성공", HttpStatus.OK,
                new PageResponseResultDTO<>(
                        memberPage.getContent(),
                        memberPage.getTotalElements(),
                        memberPage.getTotalPages()
                )
        ));
    }


    //드라이버전체조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message<PageResponseResultDTO<MemberResponseDTO>>> readAllDrivers(int size, int page) {

        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Order.desc("id")));
        Page<MemberResponseDTO> memberPage = memberRepository.findAllDriverPagination(pageable);

        return ResponseEntity.ok(new Message<>("전체 드라이버 조회 성공", HttpStatus.OK,new PageResponseResultDTO<>(
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


    public ResponseEntity<Message<AdminCarpoolInfoDTO>> readOne(Long id) {
        return ResponseEntity.ok(
                new Message<>("카풀 단일 조회 성공", HttpStatus.OK,
                        carpoolRepository.findById(id)
                                .map(carpool -> {
                                    CarpoolResponseDTO carpoolResponseDTO = new CarpoolResponseDTO(carpool);
                                    List<PassengerInfoDTO> passengerInfoDTOList = reservationRepository.findAllByPassenger(carpool.getId()).stream()
                                            .map(ReservationEntity::getMember)
                                            .map(PassengerInfoDTO::new)
                                            .collect(Collectors.toList());
                                    boolean isReport = reportRepository.existsByCarpoolId(carpool.getId());

                                    return new AdminCarpoolInfoDTO(carpoolResponseDTO, passengerInfoDTOList, isReport);
                                })
                                .orElseThrow(() -> new CustomException(ErrorCode.CARPOOL_NOT_FOUND))
                ));
    }


    //서버사이드 페이징  ex) carpools?page=2&size=5
    public ResponseEntity<Message<CarpoolResponseResultDTO>> readAllCarpool(int size, int page, LocalDateTime startDate, LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page-1,size,Sort.by(Sort.Order.desc("createdAt")));

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
    public ResponseEntity<Message<PageResponseResultDTO<ReportResponseDto>>> reportFindAll(int size, int page){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<ReportEntity> reportPage = reportRepository.findAllReports(pageable);

        PageResponseResultDTO<ReportResponseDto> pageResponseResultDTO = new PageResponseResultDTO<>(
                reportPage.getContent().stream()
                        .map(ReportResponseDto::new)
                        .collect(Collectors.toList()),
                reportPage.getTotalElements(),
                reportPage.getTotalPages()
        );

        return ResponseEntity.ok(new Message<>("신고목록 전체조회 성공", HttpStatus.OK, pageResponseResultDTO));
    }


    @Transactional
    public ResponseEntity<Message<PageResponseResultDTO<ReportResponseDto>>> readAllByCarpool(Long id, int size, int page) {
        Pageable pageable = PageRequest.of(page-1,size,Sort.by(Sort.Order.desc("createdAt")));
        Page<ReportEntity> reportsByCarpool = reportRepository.findByCarpoolId(id,pageable);

        PageResponseResultDTO<ReportResponseDto> pageResponseResultDTO = new PageResponseResultDTO<>(
                reportsByCarpool.getContent().stream()
                        .map(ReportResponseDto::new)
                        .collect(Collectors.toList()),
                reportsByCarpool.getTotalElements(),
                reportsByCarpool.getTotalPages()
        );
        return ResponseEntity.ok(new Message<>("카풀 신고목록 전체조회 성공", HttpStatus.OK, pageResponseResultDTO));

    }
    @Transactional
    public ResponseEntity<Message<Boolean>> processReport(Long reportId) {
        ReportEntity report = reportRepository.findReportById(reportId).orElseThrow(
                ()-> new CustomException(ErrorCode.REPORT_NOT_FOUND));

        report.isProcess();

        return ResponseEntity.ok(
                new Message<>("신고 처리 완료",HttpStatus.OK,report.getIsProcessed())
        );
    }

}
