package com.zone.zone01blog.dto;

import lombok.Data;

@Data
public class CreateReportRequest {
    private String reason;

    public CreateReportRequest(String reason) {
        this.reason = reason;
    }

    public CreateReportRequest() {
    }

}