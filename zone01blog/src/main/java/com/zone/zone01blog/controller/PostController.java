package com.zone.zone01blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zone.zone01blog.dto.CreatePostRequest;
import com.zone.zone01blog.dto.PostDTO;
import com.zone.zone01blog.dto.UpdatePostRequest;
import com.zone.zone01blog.service.PostService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ===== General Post Endpoints =====

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable String id) {
        PostDTO post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // ===== User-Specific Post Endpoints =====

    @PostMapping("/users/{userId}/posts")
    public ResponseEntity<PostDTO> createPost(
            @PathVariable String userId,
            @RequestBody CreatePostRequest request) {
        PostDTO createdPost = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<PostDTO>> getUserPosts(@PathVariable String userId) {
        List<PostDTO> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("users/{userId}/posts/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable String userId, @PathVariable String postId,
            @RequestBody UpdatePostRequest request) {
        PostDTO postDTO = postService.updatePost(userId, postId, request);

        return ResponseEntity.ok(postDTO);
    }

    @DeleteMapping("users/{userId}/posts/{postId}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable String userId, @PathVariable String postId) {
        postService.deletePost(userId, postId);

        return ResponseEntity.noContent().build();
    }
}
