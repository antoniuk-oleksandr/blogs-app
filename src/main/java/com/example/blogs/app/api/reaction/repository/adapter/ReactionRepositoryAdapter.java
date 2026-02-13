package com.example.blogs.app.api.reaction.repository.adapter;

import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.exception.FailedToFindReactionException;
import com.example.blogs.app.api.reaction.exception.FailedToSaveReactionException;
import com.example.blogs.app.api.reaction.exception.ReactionNotFoundException;
import com.example.blogs.app.api.reaction.exception.UserHasAlreadyReactedException;

/**
 * Adapter for reaction repository operations with exception handling.
 */
public interface ReactionRepositoryAdapter {

    /**
     * Saves a reaction entity to the database with exception translation.
     * Detects unique constraint violations for duplicate reactions.
     *
     * @param reactionEntity the reaction entity to save
     * @return the saved reaction entity with generated ID
     * @throws FailedToSaveReactionException if the save operation fails
     * @throws UserHasAlreadyReactedException if user already has a reaction on this post
     */
     ReactionEntity save(ReactionEntity reactionEntity);

    /**
     * Finds a reaction by post ID and user ID with exception translation.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     * @return the reaction entity if found
     * @throws ReactionNotFoundException if the reaction does not exist
     * @throws FailedToFindReactionException if the find operation fails
     */
     ReactionEntity findByPostIdAndUserId(Long postId, Long userId);
}
