package MATE.Carpool.domain.admin.service;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;

    //회원전체조회
    @Transactional(readOnly = true)
    public ResponseEntity<List<MemberResponseDto>> readAll() {
        List<MemberResponseDto> memberResponseDtoList = memberRepository.findAll().stream()
                .map(MemberResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(memberResponseDtoList);
    }

    @Transactional
    public ResponseEntity<String> isBanned(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        member.setIsBanned(!member.getIsBanned());

        if (member.getIsBanned()) {
            return ResponseEntity.ok(String.format("ID : %s 회원이 정지 되었습니다.", member.getMemberId()));

        }else{
            return ResponseEntity.ok(String.format("ID : %s 회원이 정지해제 되었습니다.", member.getMemberId()));
        }


    }

    //회원 정지

    //카풀내역 전체조회
    
    //신고내역 조회
}
