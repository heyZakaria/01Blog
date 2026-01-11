package com.zone.zone01blog.dto;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReportDTO {
    private String id;
    private UserDTO reporter;
    private UserDTO reportedUser;
    private String reason;
    private String status;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public ReportDTO(String id, UserDTO reporter, UserDTO reportedUser,
            String reason, String status, String adminNotes,
            LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reason = reason;
        this.status = status;
        this.adminNotes = adminNotes;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

}