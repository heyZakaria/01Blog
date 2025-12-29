package com.zone.zone01blog.dto;

import lombok.Getter;

@Getter
public class LoginResponse{
     private String token;
    private UserDTO user;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}