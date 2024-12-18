package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    // 특정 카풀에 해당하는 예약 정보를 가져오는 메서드
    List<ReservationEntity> findByCarpool(Long Id);

}