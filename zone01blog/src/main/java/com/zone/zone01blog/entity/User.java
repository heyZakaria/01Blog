package com.zone.zone01blog.entity;

import lombok.Data;

@Data
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;

    public User(String id, String name, String email, String password, String role){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password= password;
        this.role= role;
    }
}
