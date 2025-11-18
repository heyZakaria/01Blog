package com.zone.zone01blog.dto;

import lombok.Data;

@Data
public class UpdatePostRequest {
    private String title;
    private String description;

    public UpdatePostRequest(String title, String description){
        this.title = title;
        this.description = description;
    }
}
