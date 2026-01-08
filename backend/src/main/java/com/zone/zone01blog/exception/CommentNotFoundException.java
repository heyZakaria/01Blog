package com.zone.zone01blog.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}