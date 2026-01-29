package com.zone.zone01blog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zone.zone01blog.dto.CreatePostRequest;
import com.zone.zone01blog.dto.PostDTO;
import com.zone.zone01blog.dto.UpdatePostRequest;
import com.zone.zone01blog.security.JwtAuthenticationToken;
import com.zone.zone01blog.service.LikeService;
import com.zone.zone01blog.service.PostService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final LikeService likeService;

    public PostController(PostService postService, LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts(
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        List<PostDTO> posts = postService.getAllPosts(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(
            @PathVariable String id,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        PostDTO post = postService.getPostById(id, userId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO>> getFeed(
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        List<PostDTO> feed = postService.getFeed(userId);
        return ResponseEntity.ok(feed);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        PostDTO post = postService.createPost(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable String id,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        PostDTO post = postService.updatePost(id, request, userId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable String id,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable String id,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        boolean liked = likeService.toggleLike(id, userId);
        long likeCount = likeService.getLikeCount(id);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/media")
    public ResponseEntity<PostDTO> uploadMedia(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        PostDTO post = postService.uploadMedia(id, file, userId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}/media")
    public ResponseEntity<PostDTO> deleteMedia(
            @PathVariable String id,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        PostDTO post = postService.deleteMedia(id, userId);
        return ResponseEntity.ok(post);
    }
}
