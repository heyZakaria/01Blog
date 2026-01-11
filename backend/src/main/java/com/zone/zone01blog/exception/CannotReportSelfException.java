package com.zone.zone01blog.exception;

public class CannotReportSelfException extends RuntimeException {
    public CannotReportSelfException(String message) {
        super(message);
    }
}