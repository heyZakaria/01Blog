package com.zone.zone01blog.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "http://localhost:3333")
@RestController // @Controller (HTTP req/resp) + @ResponseBody (object to json by jackson)
@RequestMapping("/api/v1/")
public class user {
    
    
}


	