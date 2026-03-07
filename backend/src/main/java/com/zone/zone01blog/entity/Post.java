package com.zone.zone01blog.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Post {

    @Id
    private String id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    // fetch = FetchType.LAZY, what it means: "Don't load the author until I access
    // it"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "media_type")
    private String mediaType;

    @Column(nullable = false)
    private boolean hidden = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    
}
