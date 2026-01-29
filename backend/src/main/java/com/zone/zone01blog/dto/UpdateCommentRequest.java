package com.zone.zone01blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateCommentRequest {
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 500, message = "Content must be between 1 and 500 characters")
    private String content;

    public UpdateCommentRequest() {
    }

    public UpdateCommentRequest(String content) {
        this.content = content;
    }
}
