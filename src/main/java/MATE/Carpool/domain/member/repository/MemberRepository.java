package MATE.Carpool.domain.member.repository;

import MATE.Carpool.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberId(String memberId);

    //존재하면 true
    boolean existsByEmail(String email);
    boolean existsByMemberId(String memberId);
}
