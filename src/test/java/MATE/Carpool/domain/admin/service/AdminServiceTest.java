package MATE.Carpool.domain.admin.service;


import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import jakarta.validation.constraints.AssertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
public class AdminServiceTest {

    @Mock
    private MemberRepository memberRepository;


    @BeforeEach
    public void setUp() throws Exception {
        Member member = Member.builder()
                .memberId("test1")
                .email("test@email.com")
                .nickname("test")
                .password("test1")
                .build();
        memberRepository.save(member);

    }

    @Test
    public void isBanned() {
        String memberId = "test1";

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        member.setIsBanned(!member.getIsBanned());
        memberRepository.save(member);

        assertTrue(member.getIsBanned());
    }
}