package com.zone.zone01blog.dto;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String title;
    private String description;
    
    public CreatePostRequest() {}
    
    public CreatePostRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
}