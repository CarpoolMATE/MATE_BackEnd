package MATE.Carpool.domain.member.repository;

import MATE.Carpool.domain.member.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}
