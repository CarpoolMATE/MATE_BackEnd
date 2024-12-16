package MATE.Carpool.domain.report.repository;

import MATE.Carpool.domain.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity,Long> {


    @Query("select r from ReportEntity r join fetch r.member m join fetch r.carpool c")
    List<ReportEntity> findAllReports();


    @Query("select r from ReportEntity r join fetch r.member join fetch r.carpool c where r.id = :id")
    Optional<ReportEntity> findReportById(@Param("id")Long id);
}