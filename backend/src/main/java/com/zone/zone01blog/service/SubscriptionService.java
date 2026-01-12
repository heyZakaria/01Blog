package com.zone.zone01blog.service;

import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.NotificationType;
import com.zone.zone01blog.entity.Subscription;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.CannotFollowSelfException;
import com.zone.zone01blog.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
            UserService userService,
            NotificationService notificationService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public boolean toggleFollow(String followingId, String followerId) {
        if (followerId.equals(followingId)) {
            throw new CannotFollowSelfException("You cannot follow yourself");
        }

        User follower = userService.getUserEntityById(followerId);
        User following = userService.getUserEntityById(followingId);

        Optional<Subscription> existingSubscription = subscriptionRepository.findByFollowerIdAndFollowingId(followerId,
                followingId);

        if (existingSubscription.isPresent()) {
            subscriptionRepository.delete(existingSubscription.get());
            return false;
        } else {
            Subscription subscription = new Subscription(
                    UUID.randomUUID().toString(),
                    follower,
                    following);

            subscriptionRepository.save(subscription);

            String message = follower.getName() + " started following you";
            notificationService.createNotification(
                    following,
                    NotificationType.NEW_FOLLOWER,
                    message,
                    follower,
                    null);

            return true;
        }
    }

    public boolean isFollowing(String followerId, String followingId) {
        return subscriptionRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public List<UserDTO> getFollowing(String userId) {
        User u = userService.getUserEntityById(userId);

        List<User> followingUsers = subscriptionRepository.findFollowingByUserId(userId);
        return followingUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFollowers(String userId) {
        userService.getUserEntityById(userId);

        List<User> followers = subscriptionRepository.findFollowersByUserId(userId);
        return followers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public long getFollowingCount(String userId) {
        return subscriptionRepository.countByFollowerId(userId);
    }

    public long getFollowersCount(String userId) {
        return subscriptionRepository.countByFollowingId(userId);
    }

    public List<String> getFollowingIds(String userId) {
        List<User> followingUsers = subscriptionRepository.findFollowingByUserId(userId);
        return followingUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}