package com.zone.zone01blog.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
    private LocalDateTime timestamp;

    public User(String id, String name, String email, String password, String role, LocalDateTime timestamp) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.timestamp = timestamp;
    }
}
