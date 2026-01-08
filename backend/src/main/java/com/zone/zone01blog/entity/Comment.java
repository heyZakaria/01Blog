package com.zone.zone01blog.entity;

import jakarta.persistence.*;
import lombok.Data;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Comment {

    @Id
    private String id;

    @Column(nullable = false, length = 500)
    private String content;

    // Many comments belong to one post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Many comments belong to one user (author)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Comment(String id, String content, Post post, User author) {
        this.id = id;
        this.content = content;
        this.post = post;
        this.author = author;
    }

    public Comment() {
    }
}