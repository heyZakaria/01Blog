package com.zone.zone01blog.dto;

import lombok.Data;

@Data
public class ResolveReportRequest {
    private String status;
    private String adminNotes;
    private Boolean banUser;

    public ResolveReportRequest() {
    }

}