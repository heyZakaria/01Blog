package com.zone.zone01blog.controller;

import com.zone.zone01blog.dto.ReportDTO;
import com.zone.zone01blog.dto.ResolveReportRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.service.PostService;
import com.zone.zone01blog.service.ReportService;
import com.zone.zone01blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ReportService reportService;
    private final UserService userService;
    private final PostService postService;

    public AdminController(ReportService reportService,
            UserService userService,
            PostService postService) {
        this.reportService = reportService;
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/reports")
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/status/{status}")
    public ResponseEntity<List<ReportDTO>> getReportsByStatus(@PathVariable String status) {
        List<ReportDTO> reports = reportService.getReportsByStatus(status);
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/reports/{reportId}/resolve")
    public ResponseEntity<ReportDTO> resolveReport(
            @PathVariable String reportId,
            @RequestBody ResolveReportRequest request) {
        ReportDTO report = reportService.resolveReport(reportId, request);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable String reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<Map<String, Object>> toggleBanUser(@PathVariable String userId) {
        UserDTO user = userService.toggleBan(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("banned", true);
        response.put("message", "User banned successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        long pendingReports = reportService.getPendingReportsCount();

        analytics.put("pendingReports", pendingReports);
        analytics.put("totalUsers", userService.getAllUsers().size());

        return ResponseEntity.ok(analytics);
    }
}