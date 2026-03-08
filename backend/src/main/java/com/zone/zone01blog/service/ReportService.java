package com.zone.zone01blog.service;

import com.zone.zone01blog.dto.*;
import com.zone.zone01blog.entity.Post;
import com.zone.zone01blog.entity.Report;
import com.zone.zone01blog.entity.ReportStatus;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.CannotReportSelfException;
import com.zone.zone01blog.exception.CannotReportAdminException;
import com.zone.zone01blog.exception.PostNotFoundException;
import com.zone.zone01blog.exception.ReportNotFoundException;
import com.zone.zone01blog.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final UserService userService;

    public ReportService(ReportRepository reportRepository, PostRepository postRepository, UserService userService) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public ReportDTO createReport(String reportedUserId, CreateReportRequest request, String reporterId) {
        if (reporterId.equals(reportedUserId)) {
            throw new CannotReportSelfException("You cannot report yourself");
        }

        User reporter = userService.getUserEntityById(reporterId);
        User reportedUser = userService.getUserEntityById(reportedUserId);
        if ("ADMIN".equalsIgnoreCase(reportedUser.getRole())) {
            throw new CannotReportAdminException("You cannot report an admin user");
        }

        Report report = Report.builder()
                .id(UUID.randomUUID().toString())
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(request.getReason())
                .build();

        Report savedReport = reportRepository.save(report);
        return convertToDTO(savedReport);
    }

    public ReportDTO createPostReport(String postId, CreateReportRequest request, String reporterId) {
        Post post = postRepository.findVisibleByIdWithAuthor(postId);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }

        User reporter = userService.getUserEntityById(reporterId);
        User reportedUser = post.getAuthor();

        if (reporterId.equals(reportedUser.getId())) {
            throw new CannotReportSelfException("You cannot report your own post");
        }
        if ("ADMIN".equalsIgnoreCase(reportedUser.getRole())) {
            throw new CannotReportAdminException("You cannot report an admin user");
        }

        Report report = Report.builder()
                .id(UUID.randomUUID().toString())
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reportedPost(post)
                .reason(request.getReason())
                .build();

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
        Post reportedPost = report.getReportedPost();

        UserDTO reporterDTO = userService.convertToDTO(reporter);
        UserDTO reportedUserDTO = userService.convertToDTO(reportedUser);
        PostSummaryDTO reportedPostDTO = null;
        if (reportedPost != null) {
            UserDTO authorDTO = userService.convertToDTO(reportedPost.getAuthor());
            reportedPostDTO = new PostSummaryDTO(
                    reportedPost.getId(),
                    reportedPost.getTitle(),
                    authorDTO);
        }

        return ReportDTO.builder()
                .id(report.getId())
                .reporter(reporterDTO)
                .reportedUser(reportedUserDTO)
                .reportedPost(reportedPostDTO)
                .reason(report.getReason())
                .status(report.getStatus().name())
                .adminNotes(report.getAdminNotes())
                .createdAt(report.getCreatedAt())
                .resolvedAt(report.getResolvedAt())
                .build();
    }
}
