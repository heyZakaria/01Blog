package com.zone.zone01blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone.zone01blog.dto.CreatePostRequest;
import com.zone.zone01blog.dto.PostDTO;
import com.zone.zone01blog.dto.UpdatePostRequest;
import com.zone.zone01blog.security.JwtAuthenticationToken;
import com.zone.zone01blog.service.PostService;
import com.zone.zone01blog.util.AuthContext;

@RestController
@RequestMapping("/api/v1/")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ===== General Post Endpoints =====

    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> getPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable String id) {
        PostDTO post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // ===== User-Specific Post Endpoints =====

    @GetMapping("/users/posts")
    public ResponseEntity<List<PostDTO>> getUserPosts(@AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();

        List<PostDTO> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts")
    public ResponseEntity<PostDTO> createPost(@RequestBody CreatePostRequest postRequest,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {

        String userId = auth.getUserId();
        PostDTO createdPost = postService.createPost(userId, postRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable String postId,
            @RequestBody UpdatePostRequest postRequest, @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();

        PostDTO postDTO = postService.updatePost(userId, postId, postRequest);

        return ResponseEntity.ok(postDTO);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable String postId,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }
}
