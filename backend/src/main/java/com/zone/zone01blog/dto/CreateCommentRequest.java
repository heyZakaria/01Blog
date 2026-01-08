package com.zone.zone01blog.dto;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String content;

    public CreateCommentRequest() {
    }

    public CreateCommentRequest(String content) {
        this.content = content;
    }
}
