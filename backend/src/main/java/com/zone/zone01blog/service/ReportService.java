package com.zone.zone01blog.service;

import com.zone.zone01blog.dto.*;
import com.zone.zone01blog.entity.Report;
import com.zone.zone01blog.entity.ReportStatus;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.CannotReportSelfException;
import com.zone.zone01blog.exception.ReportNotFoundException;
import com.zone.zone01blog.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;

    public ReportService(ReportRepository reportRepository, UserService userService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
    }

    public ReportDTO createReport(String reportedUserId, CreateReportRequest request, String reporterId) {
        if (reporterId.equals(reportedUserId)) {
            throw new CannotReportSelfException("You cannot report yourself");
        }

        if (reportRepository.existsByReporterIdAndReportedUserId(reporterId, reportedUserId)) {
            throw new IllegalStateException("You have already reported this user");
        }

        User reporter = userService.getUserEntityById(reporterId);
        User reportedUser = userService.getUserEntityById(reportedUserId);

        Report report = new Report(
                UUID.randomUUID().toString(),
                reporter,
                reportedUser,
                request.getReason());

        Report savedReport = reportRepository.save(report);
        return convertToDTO(savedReport);
    }

    // // ADMIN
    public List<ReportDTO> getAllReports() {
        List<Report> reports = reportRepository.findAllWithUsers();
        return reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ADMIN
    public List<ReportDTO> getReportsByStatus(String statusStr) {
        ReportStatus status = ReportStatus.valueOf(statusStr.toUpperCase());
        List<Report> reports = reportRepository.findByStatusWithUsers(status);
        return reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ADMIN
    public long getPendingReportsCount() {
        return reportRepository.countByStatus(ReportStatus.PENDING);
    }

    // ADMIN
    public ReportDTO resolveReport(String reportId, ResolveReportRequest request) {
        Report report = reportRepository.findByIdWithUsers(reportId);
        if (report == null) {
            throw new ReportNotFoundException("Report not found with id: " + reportId);
        }

        ReportStatus newStatus = ReportStatus.valueOf(request.getStatus().toUpperCase());
        report.setStatus(newStatus);
        report.setAdminNotes(request.getAdminNotes());
        report.setResolvedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(request.getBanUser())) {
            User reportedUser = report.getReportedUser();
            reportedUser.setBanned(true);
        }

        Report updatedReport = reportRepository.save(report);
        return convertToDTO(updatedReport);
    }

    // ADMIN
    public void deleteReport(String reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException("Report not found with id: " + reportId);
        }
        reportRepository.deleteById(reportId);
    }

    private ReportDTO convertToDTO(Report report) {
        User reporter = report.getReporter();
        User reportedUser = report.getReportedUser();

        UserDTO reporterDTO = userService.convertToDTO(reporter);
        UserDTO reportedUserDTO = userService.convertToDTO(reportedUser);

        return new ReportDTO(
                report.getId(),
                reporterDTO,
                reportedUserDTO,
                report.getReason(),
                report.getStatus().name(),
                report.getAdminNotes(),
                report.getCreatedAt(),
                report.getResolvedAt());
    }
}