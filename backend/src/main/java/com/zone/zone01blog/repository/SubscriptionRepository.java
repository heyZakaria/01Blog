package com.zone.zone01blog.repository;


import com.zone.zone01blog.entity.Subscription;
import com.zone.zone01blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    @Query("SELECT s FROM Subscription s WHERE s.follower.id = :followerId AND s.following.id = :followingId")
    Optional<Subscription> findByFollowerIdAndFollowingId(String followerId, String followingId);

    boolean existsByFollowerIdAndFollowingId(String followerId, String followingId);

    @Query("SELECT s.following FROM Subscription s WHERE s.follower.id = :userId")
    List<User> findFollowingByUserId(String userId);

    @Query("SELECT s.follower FROM Subscription s WHERE s.following.id = :userId")
    List<User> findFollowersByUserId(String userId);

    long countByFollowerId(String followerId);

    long countByFollowingId(String followingId);

    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);
}