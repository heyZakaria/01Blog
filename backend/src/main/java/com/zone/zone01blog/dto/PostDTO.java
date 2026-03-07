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
public class PostDTO {
    private String id;
    private String title;
    private String description;
    // private List<Comment> comments;
    private long likeCount;
    private UserDTO author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long commentCount;
    private boolean likedByCurrentUser;
    private String mediaUrl;
    private String mediaType;
    private boolean hidden;

}
