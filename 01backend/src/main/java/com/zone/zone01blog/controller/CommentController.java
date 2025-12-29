package com.zone.zone01blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("api/v1/posts")
public class CommentController {

    // private CommentService commentService;
    @GetMapping("/{postId}")
    public String getComments(@RequestParam String postId) {
        return new String();
    }

    @PostMapping("/{postId}")
    public String createComment(@RequestParam String postId, @RequestBody String entity) {
        
        return entity;
    }


}
