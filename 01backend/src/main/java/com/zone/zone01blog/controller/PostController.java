package com.zone.zone01blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.zone.zone01blog.service.PostService;
import com.zone.zone01blog.util.AuthContext;

@RestController
@RequestMapping("/api/v1/")
public class PostController {

    private final PostService postService;
    private final AuthContext authContext;

    public PostController(PostService postService, AuthContext authContext) {
        this.postService = postService;
        this.authContext = authContext;
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

    @PostMapping("/posts")
    public ResponseEntity<PostDTO> createPost(@RequestBody CreatePostRequest postRequest) {

        String userId = authContext.getCurrentUserId();
        PostDTO createdPost = postService.createPost(userId, postRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/users/posts")
    public ResponseEntity<List<PostDTO>> getUserPosts() {
        String userId = authContext.getCurrentUserId();
        List<PostDTO> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable String postId,
            @RequestBody UpdatePostRequest postRequest) {
        String userId = authContext.getCurrentUserId();
        PostDTO postDTO = postService.updatePost(userId, postId, postRequest);

        return ResponseEntity.ok(postDTO);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<PostDTO> deletePost(@PathVariable String postId) {
        String userId = authContext.getCurrentUserId();
        postService.deletePost(userId, postId);
        return ResponseEntity.noContent().build();
    }
}
