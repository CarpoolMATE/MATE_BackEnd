package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseDTO;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import MATE.Carpool.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface CarpoolRepository extends JpaRepository<CarpoolEntity, Long> {

    @Query("SELECT c from CarpoolEntity c where c.createdAt > :blockStart")
    List<CarpoolEntity> findByAllList(@Param("blockStart")LocalDateTime blockStart);

    @Query("SELECT c from CarpoolEntity c where c.createdAt > :blockStart AND c.capacity > c.reservationCount")
    List<CarpoolEntity> findByActiveList(@Param("blockStart")LocalDateTime blockStart);

    //ASC는 오름차순, DESC는 내림차
    @Query("SELECT c FROM CarpoolEntity c WHERE c.createdAt > :blockStart ORDER BY c.departureDateTime ASC")
    List<CarpoolEntity> findByLowCostList(@Param("blockStart")LocalDateTime blockStart);

    @Query("SELECT c FROM CarpoolEntity c WHERE c.createdAt > :blockStart ORDER BY c.cost ASC")
    List<CarpoolEntity> findByFastList(@Param("blockStart")LocalDateTime blockStart);

    @Query("SELECT c from CarpoolEntity c where c.member = :member ORDER BY c.departureDateTime DESC")
    List<CarpoolEntity> findByMemberHis(@Param("member")Member member);


    @Query("SELECT c FROM CarpoolEntity c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    Page<CarpoolResponseDTO> findByCarpoolToPeriod(Pageable pageable,@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c.id FROM CarpoolEntity c WHERE c.createdAt > :blockStart ORDER BY c.departureDateTime ASC")
    List<Long> findAllTodayList(@Param("blockStart")LocalDateTime blockStart);

}
