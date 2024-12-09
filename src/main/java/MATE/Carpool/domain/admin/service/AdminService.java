package MATE.Carpool.domain.admin.service;


import MATE.Carpool.domain.member.dto.response.MemberResponseDto;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;

    //회원전체조회
    public ResponseEntity<List<MemberResponseDto>> readAll() {
        List<MemberResponseDto> memberResponseDtoList = memberRepository.findAll().stream()
                .map(MemberResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(memberResponseDtoList);
    }

    //회원 정지

    //카풀내역 전체조회
    
    //신고내역 조회
}
