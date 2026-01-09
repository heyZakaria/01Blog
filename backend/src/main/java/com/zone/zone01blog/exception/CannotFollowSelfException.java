package com.zone.zone01blog.exception;

public class CannotFollowSelfException extends RuntimeException {
    public CannotFollowSelfException(String message) {
        super(message);
    }
}