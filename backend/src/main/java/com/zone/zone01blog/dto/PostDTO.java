package com.zone.zone01blog.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class PostDTO {
    private String id;
    private String title;
    private String description;
    // private List<Comment> comments;
    private long likes;
    private UserDTO author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long commentCount;
    private boolean likedByCurrentUser;
    private String mediaUrl;
    private String mediaType;

    public PostDTO(String id, String title, String description, long likes, UserDTO author,
            LocalDateTime createdAt, LocalDateTime updatedAt, long commentCount, boolean likedByCurrentUser,
            String mediaUrl, String mediaType) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.likes = likes;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentCount = commentCount;
        this.likedByCurrentUser = likedByCurrentUser;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
