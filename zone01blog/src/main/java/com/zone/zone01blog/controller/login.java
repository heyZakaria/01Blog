package com.zone.zone01blog.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@RestController
public class login {
    
    
    private String username;
    private String password;

    public void GetLogIn(){

    }
}
