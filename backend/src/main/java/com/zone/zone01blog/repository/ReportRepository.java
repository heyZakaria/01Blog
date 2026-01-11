package com.zone.zone01blog.repository;

import com.zone.zone01blog.entity.Report;
import com.zone.zone01blog.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {

    @Query("SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.reportedUser ORDER BY r.createdAt DESC")
    List<Report> findAllWithUsers();

    @Query("SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.reportedUser WHERE r.status = :status ORDER BY r.createdAt DESC")
    List<Report> findByStatusWithUsers(ReportStatus status);

    @Query("SELECT r FROM Report r JOIN FETCH r.reporter JOIN FETCH r.reportedUser WHERE r.id = :id")
    Report findByIdWithUsers(String id);

    long countByStatus(ReportStatus status);

    boolean existsByReporterIdAndReportedUserId(String reporterId, String reportedUserId);
}