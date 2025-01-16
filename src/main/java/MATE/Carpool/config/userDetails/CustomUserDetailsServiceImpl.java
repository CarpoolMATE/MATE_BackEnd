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

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        return new CustomUserDetails(memberRepository.findByMemberId(memberId)
                .map(m -> {
                    if (m.getIsBanned()) throw new CustomException(ErrorCode.USER_IS_BANNED);
                    return m;
                })
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));

    }
}
