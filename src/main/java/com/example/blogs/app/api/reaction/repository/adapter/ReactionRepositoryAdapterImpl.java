package com.example.blogs.app.api.reaction.repository.adapter;

import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.exception.FailedToFindReactionException;
import com.example.blogs.app.api.reaction.exception.FailedToSaveReactionException;
import com.example.blogs.app.api.reaction.exception.ReactionNotFoundException;
import com.example.blogs.app.api.reaction.exception.UserHasAlreadyReactedException;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.util.SqlExceptionUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import com.example.blogs.app.api.reaction.repository.ReactionRepository;

/**
 * Wraps reaction repository operations with exception translation for consistent error handling.
 */
@Component
@AllArgsConstructor
public class ReactionRepositoryAdapterImpl implements ReactionRepositoryAdapter {

    private final ReactionRepository reactionRepository;

    private final SqlExceptionUtils sqlExceptionUtils;

    private final Logger log = LoggerFactory.getLogger(ReactionRepositoryAdapterImpl.class);

    /**
     * Saves a reaction entity to the database with exception translation.
     * Detects unique constraint violations and wraps repository exceptions.
     *
     * @param reactionEntity the reaction entity to save
     * @return the saved reaction entity with generated ID
     * @throws UserHasAlreadyReactedException if user already has a reaction on this post
     * @throws FailedToSaveReactionException if the save operation fails
     */
    @Override
    public ReactionEntity save(ReactionEntity reactionEntity) {
        try {
            return reactionRepository.save(reactionEntity);
        } catch (Exception e) {
            if (sqlExceptionUtils.containsUniqueViolation(e, "user_id")) {
                log.warn("database_operation_failed unique_violation operation=save reactionEntity={} error={} requestId={}",
                        reactionEntity, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID));
                throw new UserHasAlreadyReactedException(e);
            }

            log.error("database_operation_failed operation=save reactionEntity={} error={} requestId={}",
                    reactionEntity, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToSaveReactionException(e);
        }
    }

    /**
     * Finds a reaction by post ID and user ID with exception translation.
     * Wraps repository exceptions in domain-specific exceptions for consistent error handling.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     * @return the reaction entity if found
     * @throws ReactionNotFoundException if the reaction does not exist
     * @throws FailedToFindReactionException if the find operation fails
     */
    @Override
    public ReactionEntity findByPostIdAndUserId(Long postId, Long userId) {
        try {
            return reactionRepository.findByPostIdAndUserId(postId, userId).
                    orElseThrow(() -> new ReactionNotFoundException(null));
        } catch (ReactionNotFoundException e) {
            log.info("reaction_not_found operation=findByPostIdAndUserId postId={} userId={} error={} requestId={}",
                    postId, userId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID));
            throw e;
        } catch (Exception e) {
            log.error("database_operation_failed operation=findByPostIdAndUserId postId={} userId={} error={} requestId={}",
                    postId, userId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToFindReactionException(e);
        }
    }
}
