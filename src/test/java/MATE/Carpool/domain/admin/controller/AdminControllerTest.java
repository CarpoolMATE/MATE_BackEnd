package MATE.Carpool.domain.admin.controller;

import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Mock
    private MemberRepository memberRepository;

    //http 시뮬레이션
    @Autowired
    private MockMvc mockMvc;

    @Test
    void isBanned() throws Exception {

        // 데이터 준비
        Member member = Member.builder()
                .memberId("test1")
                .email("test@email.com")
                .nickname("test")
                .password("test1")
                .build();
        memberRepository.save(member);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/isBanned/{memberId}", "test1"))
                .andExpect(status().isOk())
                .andExpect(content().string("ID : test1 회원이 정지 되었습니다."));

        Member updatedMember = memberRepository.findByMemberId("test1")
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        assertTrue(updatedMember.getIsBanned());  // 정지 처리된 상태인지 확인
    }


    }
