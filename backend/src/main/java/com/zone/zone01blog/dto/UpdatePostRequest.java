package com.zone.zone01blog.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdatePostRequest {
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    
}
