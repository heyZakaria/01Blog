package com.zone.zone01blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.zone.zone01blog.dto.CommentDTO;
import com.zone.zone01blog.dto.CreateCommentRequest;
import com.zone.zone01blog.dto.UpdateCommentRequest;
import com.zone.zone01blog.security.JwtAuthenticationToken;
import com.zone.zone01blog.service.CommentService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable String postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable String postId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        CommentDTO comment = commentService.createComment(postId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        CommentDTO comment = commentService.updateComment(commentId, request, userId);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
