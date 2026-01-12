package com.zone.zone01blog.service;

import com.zone.zone01blog.dto.NotificationDTO;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.Notification;
import com.zone.zone01blog.entity.NotificationType;
import com.zone.zone01blog.entity.Post;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(User recipient, NotificationType type, String message,
            User relatedUser, Post relatedPost) {
        Notification notification = new Notification(
                UUID.randomUUID().toString(),
                recipient,
                type,
                message);

        notification.setRelatedUser(relatedUser);
        notification.setRelatedPost(relatedPost);

        notificationRepository.save(notification);
    }

    public List<NotificationDTO> getUserNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdWithRelatedUser(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getUnreadNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    public void markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void deleteAllUserNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdWithRelatedUser(userId);
        notificationRepository.deleteAll(notifications);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        UserDTO relatedUserDTO = null;
        if (notification.getRelatedUser() != null) {
            User relatedUser = notification.getRelatedUser();
            relatedUserDTO = new UserDTO(
                    relatedUser.getId(),
                    relatedUser.getName(),
                    relatedUser.getEmail(),
                    relatedUser.getRole(),
                    relatedUser.getCreatedAt(),
                    relatedUser.getUpdatedAt());
        }

        String relatedPostId = notification.getRelatedPost() != null
                ? notification.getRelatedPost().getId()
                : null;

        return new NotificationDTO(
                notification.getId(),
                notification.getType().name(),
                notification.getMessage(),
                relatedUserDTO,
                relatedPostId,
                notification.isRead(),
                notification.getCreatedAt());
    }
}