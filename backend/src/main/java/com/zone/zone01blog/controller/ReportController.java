package com.zone.zone01blog.controller;

import com.zone.zone01blog.dto.CreateReportRequest;
import com.zone.zone01blog.dto.ReportDTO;
import com.zone.zone01blog.security.JwtAuthenticationToken;
import com.zone.zone01blog.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/users/{userId}/report")
    public ResponseEntity<ReportDTO> reportUser(
            @PathVariable String userId,
            @Valid @RequestBody CreateReportRequest request,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String reporterId = auth.getUserId();
        ReportDTO report = reportService.createReport(userId, request, reporterId);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
}
