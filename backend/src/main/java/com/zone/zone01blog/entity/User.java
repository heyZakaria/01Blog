package com.zone.zone01blog.entity;

import java.time.LocalDateTime;

import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Data
public class User {

    // default len is 255
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public User(String id, String name, String email, String password, String role, LocalDateTime timestamp) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.timestamp = timestamp;
    }

    // JPA uses it to create objects from database
    public User() {

    }
}
