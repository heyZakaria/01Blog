package com.zone.zone01blog.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WelcomeController {

    @GetMapping("/welcome")
    public ApiResponse welcome() {
        return new ApiResponse("Wlcome to Blog API", LocalDateTime.now());
    }
}