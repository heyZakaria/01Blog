package com.zone.zone01blog.dto;

import lombok.Getter;

@Getter
public class UpdateCommentRequest {
    private String content;

    public UpdateCommentRequest() {
    }

    public UpdateCommentRequest(String content) {
        this.content = content;
    }
}