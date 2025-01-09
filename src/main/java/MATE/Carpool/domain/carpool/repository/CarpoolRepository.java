package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CarpoolRepository extends JpaRepository<CarpoolEntity, Long> {

    @Query("SELECT c from CarpoolEntity c where c.createdAt > :blockStart AND c.university =: university")
    List<CarpoolEntity> findByAllList(@Param("blockStart")LocalDateTime blockStart, @Param("university")String university);

    @Query("SELECT c from CarpoolEntity c where c.createdAt > :blockStart AND c.university = :university AND c.capacity > c.reservationCount")
    List<CarpoolEntity> findByActiveList(@Param("blockStart")LocalDateTime blockStart, @Param("university")String university);

    //ASC는 오름차순, DASC는 내림차
    @Query("SELECT c FROM CarpoolEntity c WHERE c.createdAt > :blockStart AND c.university = :university ORDER BY c.departureDateTime ASC")
    List<CarpoolEntity> findByLowCostList(@Param("blockStart")LocalDateTime blockStart, @Param("university")String university);

    @Query("SELECT c FROM CarpoolEntity c WHERE c.createdAt > :blockStart AND c.university = :university ORDER BY c.cost ASC")
    List<CarpoolEntity> findByFastList(@Param("blockStart")LocalDateTime blockStart, @Param("university")String university);

    @Query("SELECT c from CarpoolEntity c where c.member = :member")
    List<CarpoolEntity> findByMemberHis(@Param("member")Member member);
}
