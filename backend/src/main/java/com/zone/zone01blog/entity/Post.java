package com.zone.zone01blog.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ManyToAny;

import com.zone.zone01blog.dto.UserDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    private String id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer likes;

    // fetch = FetchType.LAZY, what it means: "Don't load the author until I access it"
    // LAZY (default for @ManyToOne, recommended):

    // in case of author use those:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDTO author;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Post(String id, String title, String description, Integer likes, UserDTO author, LocalDateTime timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.likes = likes;
        this.author = author;
        this.timestamp = timestamp;
    }

    public Post() {

    }
}
