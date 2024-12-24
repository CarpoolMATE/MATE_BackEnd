package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import MATE.Carpool.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    // 특정 카풀에 해당하는 예약 정보를 가져오는 메서드
    List<ReservationEntity> findByCarpoolHis(Long Id);

    @Query("select r from ReservationEntity r join fetch r.carpool c join fetch r.member m where c.id =carpoolId")
    List<ReservationEntity> findAllByCarpoolId(@Param("carpoolId") Long Id);

    //@Query("SELECT r FROM ReservationEntity r where r.Carpool AND r.member")
    @Query("SELECT r FROM ReservationEntity r WHERE r.carpool.id = :carpoolId AND r.member.id = :memberId")
    ReservationEntity findByCarpoolAndMember(@Param("carpoolId") Long carpoolId, @Param("memberId") Long memberId);

    @Query("delete from ReservationEntity r where r.carpool.id =:carpoolId")
    void deleteCarpool(@Param("carpoolId") Long carpoolId);

    @Query("SELECT from ReservationEntity r join fetch r.carpool c where r.member = :member")
    List<ReservationEntity> findByCarpoolHis(@Param("member") Member member);
}

    //JPA 역사도 공부
    //스프링JPA, 마이바티스 나온 이유
    //엔티티매니저 매서드마다 호출 , em.close()
    //초기 개발자가 작성해야했던 string  sql = " select ex from ... "을 mybatis > jpa > spring/jpa
