package com.zone.zone01blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PostSummaryDTO {
    private String id;
    private String title;
    private UserDTO author;
}
