package com.zone.zone01blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResolveReportRequest {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "PENDING|REVIEWED|RESOLVED|DISMISSED", message = "Invalid status value")
    private String status;

    @Size(max = 1000, message = "Admin notes must be at most 1000 characters")
    private String adminNotes;

    private Boolean banUser;

    public ResolveReportRequest() {
    }
}
