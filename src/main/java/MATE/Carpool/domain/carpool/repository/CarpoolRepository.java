package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CarpoolRepository extends JpaRepository<CarpoolEntity, Long> {

    //파라미터 값으로 기준 시간을 받고 그 이후에 생성된 카풀ID 넘기기
    @Query("SELECT c from CarpoolEntity c where c.createdAt > :blockStart ")
    List<CarpoolEntity> findByAllList(@Param("blockStart")LocalDateTime blockStart);

}
