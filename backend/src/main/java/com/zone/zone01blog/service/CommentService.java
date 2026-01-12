package com.zone.zone01blog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zone.zone01blog.dto.CommentDTO;
import com.zone.zone01blog.dto.CreateCommentRequest;
import com.zone.zone01blog.dto.UpdateCommentRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.Comment;
import com.zone.zone01blog.entity.NotificationType;
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
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository,
            PostRepository postRepository,
            UserService userService,
            NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.notificationService = notificationService;
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

        Comment comment = new Comment(
                UUID.randomUUID().toString(),
                request.getContent(),
                post,
                author);

        Comment savedComment = commentRepository.save(comment);

        if (!post.getAuthor().getId().equals(userId)) {
            String message = author.getName() + " commented on your post: " + post.getTitle();
            notificationService.createNotification(
                    post.getAuthor(),
                    NotificationType.POST_COMMENT,
                    message,
                    author,
                    post);
        }

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

        commentRepository.deleteById(commentId);
    }

    public long getCommentCount(String postId) {
        return commentRepository.countByPostId(postId);
    }

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