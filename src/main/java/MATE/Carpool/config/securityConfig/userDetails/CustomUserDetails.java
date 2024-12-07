package MATE.Carpool.config.securityConfig.userDetails;


import MATE.Carpool.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * record 공부해보기
 */
@Getter
@SuppressWarnings("ClassCanBeRecord")
public class CustomUserDetails implements UserDetails {

    private final Member member;
    private final String memberId;



    public CustomUserDetails(Member member, String memberId) {
        this.member = member;
        this.memberId = memberId;

    }

    public String getNickname() {
        return member.getNickname();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(()->member.getMemberType().getRole());
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getMemberId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
