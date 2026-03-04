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

    public List<PostDTO> getAllPostsForAdmin() {
        List<Post> posts = postRepository.findAllWithAuthorsIncludingHidden();
        return posts.stream()
                .map(post -> convertToDTO(post, null))
                .collect(Collectors.toList());
    }


    // ADMIN
    public long getTotalPostsCount() {
        return postRepository.count();
    }

    public PostDTO getPostById(String id, String currentUserId) {
        Post post = postRepository.findVisibleByIdWithAuthor(id);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + id);
        }
        return convertToDTO(post, currentUserId);
    }

    public List<PostDTO> getFeed(String currentUserId) {
        List<String> authorIds = new ArrayList<>(subscriptionService.getFollowingIds(currentUserId));
        if (!authorIds.contains(currentUserId)) {
            authorIds.add(currentUserId);
        }

        List<Post> feedPosts = postRepository.findFeedPostsByFollowingIds(authorIds);

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

        String title = sanitizeAndValidateText(request.getTitle(), "Title", 3, 150);
        String description = sanitizeAndValidateText(request.getDescription(), "Description", 10, 1000);

        Post post = new Post(
                UUID.randomUUID().toString(),
                title,
                description,
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

        if (request.getTitle() != null) {
            post.setTitle(sanitizeAndValidateText(request.getTitle(), "Title", 3, 150));
        }
        if (request.getDescription() != null) {
            post.setDescription(sanitizeAndValidateText(request.getDescription(), "Description", 10, 1000));
        }

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

        deletePostEntity(post);
    }

    public PostDTO setPostHidden(String postId, boolean hidden) {
        Post post = postRepository.findByIdWithAuthor(postId);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        post.setHidden(hidden);
        Post updated = postRepository.save(post);
        return convertToDTO(updated, null);
    }

    public void deletePostAsAdmin(String postId) {
        Post post = postRepository.findByIdWithAuthor(postId);
        if (post == null) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        deletePostEntity(post);
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

        FileStorageService.StoredFile storedFile = fileStorageService.storeFile(file);
        String mediaType = fileStorageService.getMediaType(storedFile.contentType());

        post.setMediaUrl("/api/v1/media/" + storedFile.filename());
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
                post.getMediaType(),
                post.isHidden());
    }

    private String extractFilenameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private void deletePostEntity(Post post) {
        if (post.getMediaUrl() != null) {
            String filename = extractFilenameFromUrl(post.getMediaUrl());
            fileStorageService.deleteFile(filename);
        }
        postRepository.deleteById(post.getId());
    }

    private String sanitizeAndValidateText(String input, String fieldName, int min, int max) {
        if (input == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        // Normalize line breaks and remove control characters (except \n and \t)
        String normalized = input.replace("\r\n", "\n").replace("\r", "\n");
        normalized = normalized.replaceAll("[\\p{Cc}&&[^\n\t]]", "");

        String trimmed = normalized.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }

        String sanitized = sanitizeHtml(trimmed);
        String textOnly = stripHtml(sanitized).replace("&nbsp;", " ").trim();
        if (textOnly.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }

        if (textOnly.length() < min || textOnly.length() > max) {
            throw new IllegalArgumentException(
                    fieldName + " must be between " + min + " and " + max + " characters");
        }

        return sanitized;
    }

    private String sanitizeHtml(String input) {
        String sanitized = input;
        // Remove script tags
        sanitized = sanitized.replaceAll("(?is)<script.*?>.*?</script>", "");
        // Remove iframe/embed tags
        sanitized = sanitized.replaceAll("(?is)<(iframe|embed|object).*?>.*?</(iframe|embed|object)>", "");
        // Remove inline event handlers (onload, onclick, etc.)
        sanitized = sanitized.replaceAll("(?i)\\son\\w+\\s*=\\s*(['\"]).*?\\1", "");
        return sanitized;
    }

    private String stripHtml(String input) {
        return input.replaceAll("(?is)<[^>]*>", "");
    }

}
