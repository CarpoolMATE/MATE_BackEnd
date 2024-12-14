package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN FETCH r.carpool c " +
            "WHERE r.member.id = :memberId " +
            "AND r.createdAt BETWEEN :blockStart AND :blockEnd")
    List<ReservationEntity> findReservationsByMemberIdAndTimeBlock(
            @Param("memberId") Long memberId,
            @Param("blockStart") LocalDateTime blockStart,
            @Param("blockEnd") LocalDateTime blockEnd
    );

    List<ReservationEntity> findByCarpoolId(Long carpoolId);
}
