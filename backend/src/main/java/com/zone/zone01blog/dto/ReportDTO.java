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
public class ReportDTO {
    private String id;
    private UserDTO reporter;
    private UserDTO reportedUser;
    private String reason;
    private String status;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

}
