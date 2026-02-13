package com.example.blogs.app.api.reaction.service;

import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.entity.ReactionType;

/**
 * Service for reaction-related business operations.
 */
public interface ReactionService {

    /**
     * Sets or updates a user's reaction on a post.
     * If the user has already reacted, updates the reaction type.
     * If the user hasn't reacted yet, creates a new reaction.
     *
     * @param postId the ID of the post to react to
     * @param userId the ID of the user making the reaction
     * @param reactionType the type of reaction (LIKE or DISLIKE)
     * @return the created or updated reaction as DTO
     */
    ReactionDTO setReaction(Long postId, Long userId, ReactionType reactionType);
}
