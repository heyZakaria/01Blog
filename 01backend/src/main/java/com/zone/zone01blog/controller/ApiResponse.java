package com.zone.zone01blog.controller;

import java.time.LocalDateTime;

public class ApiResponse {
    private String message;
    private LocalDateTime timestamp;

    public ApiResponse(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

}
