package com.zone.zone01blog.dto;

import lombok.Data;

@Data
public class    UpdateUserRequest {
    private String name;
    private String email;
    private String password;
    private String role;

    public UpdateUserRequest(String name, String email, String password, String role){
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
