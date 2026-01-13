package com.zone.zone01blog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zone.zone01blog.dto.CreatePostRequest;
import com.zone.zone01blog.dto.PostDTO;
import com.zone.zone01blog.dto.UpdatePostRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.NotificationType;
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
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;

    public PostService(PostRepository postRepository,
            UserService userService,
            CommentService commentService,
            LikeService likeService,
            SubscriptionService subscriptionService,
            NotificationService notificationService,
            FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.subscriptionService = subscriptionService;
        this.notificationService = notificationService;
        this.fileStorageService = fileStorageService;

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

    public List<PostDTO> getFeed(String currentUserId) {
        List<String> followingIds = subscriptionService.getFollowingIds(currentUserId);

        if (followingIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Post> feedPosts = postRepository.findFeedPostsByFollowingIds(followingIds);

        return feedPosts.stream()
                .map(post -> convertToDTO(post, currentUserId))
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPostsByUserId(String userId, String currentUserId) {
        userService.getUserEntityById(userId);

        List<Post> userPosts = postRepository.findByAuthorIdWithAuthor(userId);

        return userPosts.stream()
                .map(post -> convertToDTO(post, currentUserId))
                .collect(Collectors.toList());
    }

    public PostDTO createPost(CreatePostRequest request, String userId) {
        User author = userService.getUserEntityById(userId);

        Post post = new Post(
                UUID.randomUUID().toString(),
                request.getTitle(),
                request.getDescription(),
                author);

        Post savedPost = postRepository.save(post);

        List<User> followers = subscriptionService.getFollowers(userId).stream()
                .map(dto -> userService.getUserEntityById(dto.getId()))
                .collect(Collectors.toList());

        for (User follower : followers) {
            String message = author.getName() + " created a new post: " + savedPost.getTitle();
            notificationService.createNotification(
                    follower,
                    NotificationType.NEW_POST,
                    message,
                    author,
                    savedPost);
        }

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

        if (post.getMediaUrl() != null) {
            String filename = extractFilenameFromUrl(post.getMediaUrl());
            fileStorageService.deleteFile(filename);
        }

        postRepository.deleteById(id);
    }

    public PostDTO uploadMedia(String postId, MultipartFile file, String userId) {
        Post post = postRepository.findByIdWithAuthor(postId);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only upload media to your own posts");
        }

        if (post.getMediaUrl() != null) {
            String oldFilename = extractFilenameFromUrl(post.getMediaUrl());
            fileStorageService.deleteFile(oldFilename);
        }

        String filename = fileStorageService.storeFile(file);
        String mediaType = fileStorageService.getMediaType(file.getContentType());

        post.setMediaUrl("/api/v1/media/" + filename);
        post.setMediaType(mediaType);

        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost, userId);
    }

    public PostDTO deleteMedia(String postId, String userId) {
        Post post = postRepository.findByIdWithAuthor(postId);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only delete media from your own posts");
        }

        if (post.getMediaUrl() != null) {
            String filename = extractFilenameFromUrl(post.getMediaUrl());
            fileStorageService.deleteFile(filename);

            post.setMediaUrl(null);
            post.setMediaType(null);
        }

        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost, userId);
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
                likedByCurrentUser,
                post.getMediaUrl(),
                post.getMediaType());
    }

    private String extractFilenameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

}
