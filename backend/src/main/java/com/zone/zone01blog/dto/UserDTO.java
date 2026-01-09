package com.zone.zone01blog.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long followersCount;
    private Long followingCount;
    private Boolean isFollowedByCurrentUser;

    public UserDTO(String id, String name, String email, String role,
            LocalDateTime createdAt, LocalDateTime updatedAt, Long followersCount, Long followingCount,
            Boolean isFollowedByCurrentUser) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.isFollowedByCurrentUser = isFollowedByCurrentUser;
    }

    public UserDTO(String id, String name, String email, String role,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, name, email, role, createdAt, updatedAt, null, null, null);
    }
}
