package MATE.Carpool.config.userDetails;

import MATE.Carpool.common.exception.CustomException;
import MATE.Carpool.common.exception.ErrorCode;
import MATE.Carpool.domain.member.entity.Member;
import MATE.Carpool.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow( () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (member.getIsBanned()) {
            throw new CustomException(ErrorCode.USER_IS_BANNED);

        }
        return new CustomUserDetails(member);
    }
}

// JWT 토큰에서 memberId를 추출 (가능한 이유는: setSubject에서 getName을 넣었기 때문에, JWT에는 id를 넣기 때문에 그걸 그냥 추출)
// 그 다음에 그 아이디를 가지고 loadUser 메서드를 통해서 UserDetails를 가지고옴
