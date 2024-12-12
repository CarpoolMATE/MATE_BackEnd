package MATE.Carpool.domain.member.repository;

import MATE.Carpool.domain.member.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query("SELECT d FROM Driver d JOIN FETCH d.member WHERE d.member.id = :memberId")
    Optional<Driver> findByMemberId(@Param("memberId") Long memberId);
}
