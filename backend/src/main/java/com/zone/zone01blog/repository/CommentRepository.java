package com.zone.zone01blog.repository;

import com.zone.zone01blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    // Find all comments for a specific post (with authors loaded)
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findByPostIdWithAuthors(String postId);

    // Find comment by ID with author loaded
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.id = :id")
    Comment findByIdWithAuthor(String id);

    long countByPostId(String postId);
}