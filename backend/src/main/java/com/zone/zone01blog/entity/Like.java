package com.zone.zone01blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "user_id", "post_id" })
})
@EntityListeners(AuditingEntityListener.class)
public class Like {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    

    // equals and hashCode for composite uniqueness
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Like like = (Like) o;
        return Objects.equals(user, like.user) &&
                Objects.equals(post, like.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, post);
    }
}
