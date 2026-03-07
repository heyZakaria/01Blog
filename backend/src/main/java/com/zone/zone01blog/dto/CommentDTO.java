package com.zone.zone01blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CommentDTO {
    private String id;
    private String content;
    private UserDTO author;
    private String postId;
    private String postTitle;
    private boolean hidden;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
