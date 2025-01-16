package MATE.Carpool.config.jwt;

import MATE.Carpool.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(String memberId);

    @Modifying
    @Query("delete from RefreshToken r where r.memberId = :memberId")
    void deleteMemberId(@Param("memberId") String memberId);
}
