package com.zone.zone01blog.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class) // 🆕 Enable auditing
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
    private boolean banned = false;

    @Column(nullable = false)
    private boolean suspended = false;

    @Builder.Default
    @Column(nullable = true)
    private Long tokenVersion = 0L;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isBanned() {
        return banned;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public long getTokenVersionSafe() {
        return tokenVersion == null ? 0L : tokenVersion;
    }
}
