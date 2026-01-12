package com.zone.zone01blog.service;

import com.zone.zone01blog.entity.Like;
import com.zone.zone01blog.entity.NotificationType;
import com.zone.zone01blog.entity.Post;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.PostNotFoundException;
import com.zone.zone01blog.repository.LikeRepository;
import com.zone.zone01blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public LikeService(LikeRepository likeRepository,
            PostRepository postRepository,
            UserService userService,
            NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public boolean toggleLike(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        Optional<Like> existingLike = likeRepository.findByUserIdAndPostId(userId, postId);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            User user = userService.getUserEntityById(userId);

            Like newLike = new Like(
                    UUID.randomUUID().toString(),
                    user,
                    post);

            likeRepository.save(newLike);

            if (!post.getAuthor().getId().equals(userId)) {
                String message = user.getName() + " liked your post: " + post.getTitle();
                notificationService.createNotification(
                        post.getAuthor(),
                        NotificationType.POST_LIKE,
                        message,
                        user,
                        post);
            }

            return true;
        }
    }

    public boolean hasUserLikedPost(String postId, String userId) {
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    public long getLikeCount(String postId) {
        return likeRepository.countByPostId(postId);
    }
}