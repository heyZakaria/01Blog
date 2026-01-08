package com.zone.zone01blog.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class PostDTO {
    private String id;
    private String title;
    private String description;
    // private List<Comment> comments;
    private Integer likes;
    private UserDTO author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long commentCount;

    public PostDTO(String id, String title, String description, Integer likes, UserDTO author,
            LocalDateTime createdAt, LocalDateTime updatedAt, long commentCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.likes = likes;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentCount = commentCount;
    }
}
