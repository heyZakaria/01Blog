package com.zone.zone01blog.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePostRequest {
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    public UpdatePostRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
