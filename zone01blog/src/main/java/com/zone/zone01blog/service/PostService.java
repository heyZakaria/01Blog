package com.zone.zone01blog.service;

import java.util.List;
import java.util.Optional;
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

@Service
public class PostService {

    private UserService userService;
    private PostRepository postRepository;

    public PostService(UserService userService, PostRepository postRepository) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public PostDTO createPost(String userId, CreatePostRequest request) {
        // 1. Verify user exists (will throw UserNotFoundException if not)
        userService.getUserById(userId);

        // 2. Generate ID and create Post entity
        String postId = UUID.randomUUID().toString();
        Post post = new Post(
                postId,
                request.getTitle(),
                request.getDescription(),
                0, // New post starts with 0 likes
                userId);

        // 3. Save
        Post savedPost = postRepository.save(post);

        // 4. Convert to DTO and return
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
        // Verify user exists
        userService.getUserById(userId);

        return postRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PostDTO convertToDTO(Post post) {
        // Fetch author info
        UserDTO author = userService.getUserById(post.getUserId());

        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getLikes(),
                author);
    }

    public PostDTO updatePost(String userId, String postId, UpdatePostRequest request) {
        userService.getUserById(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        if (!post.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You cannot edit someone else's post");
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());

        postRepository.update(post);

        return convertToDTO(post);
    }

    public void deletePost(String userId, String postId) {
        userService.getUserById(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

                if(!post.getUserId().equals(userId)){
                    throw new UnauthorizedAccessException("You cannot edit someone else's post");
                }
                

        postRepository.delete(postId);
    }
}
