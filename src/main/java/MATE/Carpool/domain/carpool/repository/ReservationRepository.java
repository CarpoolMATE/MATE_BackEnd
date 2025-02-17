package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    // 특정 카풀에 해당하는 예약 정보를 가져오는 메서드
    List<ReservationEntity> findByCarpoolId(Long Id);

    @Query("select r.member.id from ReservationEntity r where r.carpool.id = :carpoolId")
    List<Long> findByMemberId(@Param("carpoolId") Long carpoolId);

    @Query("select r from ReservationEntity r join fetch r.carpool c join fetch r.member m where r.carpool.id = :carpoolId")
    List<ReservationEntity> findAllByCarpoolId(@Param("carpoolId") Long Id, Pageable pageable);

    //@Query("SELECT r FROM ReservationEntity r where r.Carpool AND r.member")
    @Query("SELECT r FROM ReservationEntity r WHERE r.carpool.id = :carpoolId AND r.member.id = :memberId")
    ReservationEntity findByCarpoolAndMember(@Param("carpoolId") Long carpoolId, @Param("memberId") Long memberId);

    @Modifying
    @Query("delete from ReservationEntity r where r.carpool.id =:carpoolId")
    void deleteCarpool(@Param("carpoolId") Long carpoolId);

    @Query("SELECT r from ReservationEntity r join fetch r.carpool c where r.member = :member ORDER BY c.departureDateTime DESC")
    List<ReservationEntity> findByCarpoolHis(@Param("member") Member member);


//    @Query("select r from ReservationEntity r where r.carpoolId = :carpoolId")
//    List<ReservationEntity> findByCarpoolIdList(@Param("carpoolId") Long getCarpoolId, Pageable pageable);
}