package com.zone.zone01blog.repository;

import com.zone.zone01blog.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    @Query("SELECT n FROM Notification n " +
            "LEFT JOIN FETCH n.relatedUser " +
            "WHERE n.user.id = :userId " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdWithRelatedUser(String userId);

    long countByUserIdAndIsRead(String userId, boolean isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    void markAllAsReadByUserId(String userId);

    @Query("SELECT n FROM Notification n " +
            "LEFT JOIN FETCH n.relatedUser " +
            "WHERE n.user.id = :userId AND n.isRead = false " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(String userId);
}