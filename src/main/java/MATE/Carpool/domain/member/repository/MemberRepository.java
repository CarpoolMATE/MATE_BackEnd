package MATE.Carpool.domain.member.repository;

import MATE.Carpool.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByMemberId(String memberId);

    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.reservation = false, m.carpoolId = null")
    void updateReservationAndCarpoolId();

    //존재하면 true
    boolean existsByEmail(String email);
    boolean existsByMemberId(String memberId);

    boolean existsByNickname(String nickname);

    Long countByNickname(String nickname);
}
