package com.example.blogs.app.api.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Revoked JWT token entity with automatic timestamp tracking and expiration indexing.
 * Stores hashed refresh tokens that have been explicitly revoked before their natural expiration.
 * Includes an index on expires_at to optimize scheduled cleanup operations.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "revoked_tokens",
        indexes = @Index(name = "idx_revoked_token_expires_at", columnList = "expires_at")
)
public class RevokedTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @CreationTimestamp
    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
