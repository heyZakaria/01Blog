package com.zone.zone01blog.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Post {
    private String id;
    private String title;
    private String description;
    private Integer likes;
    private String userId;
    private LocalDateTime timestamp;

    public Post(String id, String title, String description, Integer likes, String userId, LocalDateTime timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.likes = likes;
        this.userId = userId;
        this.timestamp = timestamp;
    }
}
