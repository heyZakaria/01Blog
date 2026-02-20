package com.zone.zone01blog.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDTO {
    private String id;
    private String content;
    private UserDTO author;
    private String postId;
    private String postTitle;
    private boolean hidden;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentDTO(String id, String content, UserDTO author,
            String postId, String postTitle, boolean hidden,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.postId = postId;
        this.postTitle = postTitle;
        this.hidden = hidden;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
