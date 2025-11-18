package com.zone.zone01blog.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String role;

    public UserDTO(String id, String name, String email, String role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.role= role;
    }
}
