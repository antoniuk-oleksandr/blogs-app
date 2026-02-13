package com.example.blogs.app.api.reaction.entity;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a user's reaction to a post.
 * Tracks LIKE or DISLIKE reactions with timestamps.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "reactions",
        indexes = {
                @Index(name = "idx_post_reactions_post_id", columnList = "post_id"),
                @Index(name = "idx_post_reactions_user_id", columnList = "user_id")
        }
)
public class ReactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reactions_user")
    )
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    @JoinColumn(
            name = "post_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reactions_post")
    )
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PostEntity post;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

