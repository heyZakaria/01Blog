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

@Transactional
@Service
public class PostService {

    private UserService userService;
    private PostRepository postRepository;
    private final CommentService commentService;

    public PostService(UserService userService, PostRepository postRepository, CommentService commentService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentService = commentService;
    }

    public PostDTO createPost(String userId, CreatePostRequest request) {
        User author = userService.getUserEntityById(userId);

        String postId = UUID.randomUUID().toString();

        Post post = new Post(
                postId,
                request.getTitle(),
                request.getDescription(),
                0,
                author);

        Post savedPost = postRepository.save(post);

        return convertToDTO(savedPost);
    }

    public PostDTO getPostById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

        return convertToDTO(post);
    }

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPostsByUserId(String userId) {
        userService.getUserById(userId);

        return postRepository.findById(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PostDTO updatePost(String userId, String postId, UpdatePostRequest request) {
        // UserDTO author = userService.getUserById(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " +
                        postId));
        // Post post = postRepository.findByIdWithAuthor(postId);
        // if (post == null){
        // throw new PostNotFoundException("Post not found with id: " + postId);
        // }

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You cannot edit someone else's post");
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());

        postRepository.save(post);

        return convertToDTO(post);
    }

    public void deletePost(String userId, String postId) {
        // we can also use this
        // Post post = postRepository.findByIdWithAuthor(postId);
        // if (post == null) {
        // throw new UnauthorizedAccessException("You can only delete your own posts");
        // }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " +
                        postId));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You cannot edit someone else's post");
        }

        postRepository.deleteById(postId);
    }

    private PostDTO convertToDTO(Post post) {
        // Fetch author info
        UserDTO author = userService.getUserById(post.getAuthor().getId());

        long commentCount = commentService.getCommentCount(post.getId());

        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getLikes(),
                author,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                commentCount);
    }
}
