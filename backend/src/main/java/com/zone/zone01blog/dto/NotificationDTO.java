package com.zone.zone01blog.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NotificationDTO {
    private String id;
    private String type;
    private String message;
    private UserDTO relatedUser; // li jaya mn 3ando action
    private String relatedPostId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationDTO(String id, String type, String message,
            UserDTO relatedUser, String relatedPostId,
            boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.relatedUser = relatedUser;
        this.relatedPostId = relatedPostId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

}