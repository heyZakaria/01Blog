package com.zone.zone01blog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zone.zone01blog.dto.CommentDTO;
import com.zone.zone01blog.dto.CreateCommentRequest;
import com.zone.zone01blog.dto.UpdateCommentRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.Comment;
import com.zone.zone01blog.entity.Post;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.CommentNotFoundException;
import com.zone.zone01blog.exception.PostNotFoundException;
import com.zone.zone01blog.exception.UnauthorizedAccessException;
import com.zone.zone01blog.repository.CommentRepository;
import com.zone.zone01blog.repository.PostRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository,
            PostRepository postRepository,
            UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public List<CommentDTO> getCommentsByPostId(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }

        List<Comment> comments = commentRepository.findByPostIdWithAuthors(postId);
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO createComment(String postId, CreateCommentRequest request, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        User author = userService.getUserEntityById(userId);

        // 3. Create comment
        Comment comment = new Comment(
                UUID.randomUUID().toString(),
                request.getContent(),
                post,
                author);

        // 4. Save
        Comment savedComment = commentRepository.save(comment);

        // 5. Convert to DTO
        return convertToDTO(savedComment);
    }

    public CommentDTO updateComment(String commentId, UpdateCommentRequest request, String userId) {
        Comment comment = commentRepository.findByIdWithAuthor(commentId);
        if (comment == null) {
            throw new CommentNotFoundException("Comment not found with id: " + commentId);
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only update your own comments");
        }

        comment.setContent(request.getContent());

        Comment updatedComment = commentRepository.save(comment);

        return convertToDTO(updatedComment);
    }

    public void deleteComment(String commentId, String userId) {
        Comment comment = commentRepository.findByIdWithAuthor(commentId);
        if (comment == null) {
            throw new CommentNotFoundException("Comment not found with id: " + commentId);
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only delete your own comments");
        }

        // 3. Delete
        commentRepository.deleteById(commentId);
    }

    // Get comment count for a post
    public long getCommentCount(String postId) {
        return commentRepository.countByPostId(postId);
    }

    // Convert Comment entity to CommentDTO
    private CommentDTO convertToDTO(Comment comment) {
        User author = comment.getAuthor();

        UserDTO authorDTO = new UserDTO(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getRole(),
                author.getCreatedAt(),
                author.getUpdatedAt());

        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                authorDTO,
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}