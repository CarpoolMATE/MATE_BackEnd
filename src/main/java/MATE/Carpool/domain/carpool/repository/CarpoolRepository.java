package MATE.Carpool.domain.carpool.repository;

import MATE.Carpool.domain.carpool.entity.CarpoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarpoolRepository extends JpaRepository<CarpoolEntity, Long> {
}
