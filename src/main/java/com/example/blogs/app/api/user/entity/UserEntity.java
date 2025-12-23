package com.example.blogs.app.api.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * User entity with unique constraints on username and email.
 * Includes automatic timestamp management for audit tracking.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 500, nullable = true)
    private String bio;

    @Column(name = "profile_picture_url", nullable = true)
    private String profilePictureUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public UserEntity() {
    }

    /**
     * All-args constructor for creating a complete user entity.
     *
     * @param id the unique identifier
     * @param username the unique username
     * @param passwordHash the BCrypt-hashed password
     * @param email the unique email address
     * @param bio optional user biography
     * @param profilePictureUrl optional profile picture URL
     * @param createdAt timestamp when the user was created
     * @param updatedAt timestamp when the user was last updated
     */
    public UserEntity(Long id, String username, String passwordHash, String email, String bio,
                     String profilePictureUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
