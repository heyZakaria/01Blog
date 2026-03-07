package com.zone.zone01blog.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NotificationDTO {
    private String id;
    private String type;
    private String message;
    private UserDTO relatedUser; // li jaya mn 3ando action
    private String relatedPostId;
    private boolean isRead;
    private LocalDateTime createdAt;

}
