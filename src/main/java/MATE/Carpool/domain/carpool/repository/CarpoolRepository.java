package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.dto.response.CarpoolResponseListDTO;
import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CarpoolRepository extends JpaRepository<CarpoolEntity, Long> {


    @Query("SELECT c from CarpoolEntity c where c.createdAt > :blockStart ")
    List<CarpoolEntity> findByAllList(@Param("blockStart")LocalDateTime blockStart);
}
