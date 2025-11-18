package com.zone.zone01blog.dto;

import com.zone.zone01blog.dto.UserDTO;

import lombok.Data;

@Data
public class PostDTO {
    private String id;
    private String title;
    private String description;
    private Integer likes;
    private UserDTO author;

    public PostDTO(String id, String title, String description, Integer likes, UserDTO author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.likes = likes;
        this.author = author;
    }
}
