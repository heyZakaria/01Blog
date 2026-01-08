package com.zone.zone01blog.repository;

import com.zone.zone01blog.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {

    // Check if user already liked a post
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
    Optional<Like> findByUserIdAndPostId(String userId, String postId);

    // Check if like exists (more efficient than findBy)
    boolean existsByUserIdAndPostId(String userId, String postId);

    long countByPostId(String postId);

    void deleteByUserIdAndPostId(String userId, String postId);
}