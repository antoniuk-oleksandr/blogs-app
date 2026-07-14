package com.example.blogs.app.api.reaction.repository;

import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA repository for managing reaction persistence operations.
 */
public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {

    /**
     * Finds a reaction by post ID and user ID.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     * @return optional containing the reaction if found, empty otherwise
     */
    Optional<ReactionEntity> findByPostIdAndUserId(Long postId, Long userId);

    long countByPostIdAndReactionType(Long postId, ReactionType reactionType);
}
