package com.zone.zone01blog.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.zone.zone01blog.dto.CreatePostRequest;
import com.zone.zone01blog.dto.PostDTO;
import com.zone.zone01blog.dto.UpdatePostRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.Post;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.PostNotFoundException;
import com.zone.zone01blog.exception.UnauthorizedAccessException;
import com.zone.zone01blog.repository.PostRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostService(PostRepository postRepository,
            UserService userService,
            CommentService commentService,
            LikeService likeService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    public List<PostDTO> getAllPosts(String currentUserId) {
        List<Post> posts = postRepository.findAllWithAuthors();
        return posts.stream()
                .map(post -> convertToDTO(post, currentUserId))
                .collect(Collectors.toList());
    }

    public PostDTO getPostById(String id, String currentUserId) {
        Post post = postRepository.findByIdWithAuthor(id);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + id);
        }
        return convertToDTO(post, currentUserId);
    }

    public PostDTO createPost(CreatePostRequest request, String userId) {
        User author = userService.getUserEntityById(userId);

        Post post = new Post(
                UUID.randomUUID().toString(),
                request.getTitle(),
                request.getDescription(),
                author
        );

        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost, userId);
    }

    public PostDTO updatePost(String id, UpdatePostRequest request, String userId) {
        Post post = postRepository.findByIdWithAuthor(id);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + id);
        }

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only update your own posts");
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());

        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost, userId);
    }

    public void deletePost(String id, String userId) {
        Post post = postRepository.findByIdWithAuthor(id);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + id);
        }

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only delete your own posts");
        }

        postRepository.deleteById(id);
    }

    private PostDTO convertToDTO(Post post, String currentUserId) {
        User author = post.getAuthor();

        UserDTO authorDTO = new UserDTO(
                author.getId(),
                author.getName(),
                author.getEmail(),
                author.getRole(),
                author.getCreatedAt(),
                author.getUpdatedAt());

        long likeCount = likeService.getLikeCount(post.getId());

        long commentCount = commentService.getCommentCount(post.getId());

        boolean likedByCurrentUser = currentUserId != null &&
                likeService.hasUserLikedPost(post.getId(), currentUserId);

        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                likeCount,
                authorDTO,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                commentCount,
                likedByCurrentUser
        );
    }
}