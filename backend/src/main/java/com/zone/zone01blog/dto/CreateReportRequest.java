package com.zone.zone01blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateReportRequest {
    @NotBlank(message = "Reason is required")
    @Size(min = 5, max = 1000, message = "Reason must be between 5 and 1000 characters")
    private String reason;

    
}
