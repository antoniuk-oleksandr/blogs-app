package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.exception.FailedToCheckCommentExistenceException;
import com.example.blogs.app.api.comment.exception.FailedToCreateCommentException;
import com.example.blogs.app.api.comment.exception.FailedToDeleteCommentException;
import com.example.blogs.app.api.comment.exception.FailedToFindCommentsByPostIdException;
import com.example.blogs.app.api.comment.repository.CommentRepository;
import com.example.blogs.app.logging.MDCKeys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Wraps comment repository operations with exception translation for consistent error handling.
 */
@Component
@AllArgsConstructor
public class CommentRepositoryAdapterImpl implements CommentRepositoryAdapter {

    private static final Logger log = LoggerFactory.getLogger(CommentRepositoryAdapterImpl.class);

    private final CommentRepository commentRepository;

    /**
     * Retrieves all comments associated with a specific post with exception translation.
     * Wraps repository exceptions in a domain-specific exception for consistent error handling.
     *
     * @param postId the ID of the post
     * @return list of comments for the post
     * @throws FailedToFindCommentsByPostIdException if the repository operation fails
     */
    @Override
    public List<CommentEntity> findAllByPostId(Long postId) {
        try {
            return commentRepository.findAllByPostId(postId);
        } catch (Exception e) { 
            log.error("database_operation_failed operation=findAllByPostId postId={} error={} requestId={}",
                    postId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToFindCommentsByPostIdException(e);
        }
    }

    /**
     * Saves a comment entity to the database with exception translation.
     * Wraps repository exceptions in a domain-specific exception for consistent error handling.
     *
     * @param commentEntity the comment entity to save
     * @return the saved comment entity with generated ID
     * @throws FailedToCreateCommentException if the save operation fails
     */
    @Override
    public CommentEntity save(CommentEntity commentEntity) {
        try {
            return commentRepository.save(commentEntity);
        } catch (Exception e) {
            log.error("database_operation_failed operation=save commentEntity={} error={} requestId={}",
                    commentEntity, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToCreateCommentException(e);
        }
    }

    @Override
    public boolean existsById(Long commentId) {
        try {
            return commentRepository.existsById(commentId);
        } catch (Exception e) {
            log.error("database_operation_failed operation=existsById commentId={} error={} requestId={}",
                    commentId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToCheckCommentExistenceException(e);
        }
    }

    @Override
    public boolean existsByIdAndAuthorId(Long commentId, Long authorId) {
        try {
            return commentRepository.existsByIdAndAuthorId(commentId, authorId);
        } catch (Exception e) {
            log.error("database_operation_failed operation=existsByIdAndAuthorId commentId={} authorId={} error={} requestId={}",
                    commentId, authorId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToCheckCommentExistenceException(e);
        }
    }

    @Override
    public void deleteById(Long commentId) {
        try {
            commentRepository.deleteById(commentId);
        } catch (Exception e) {
            log.error("database_operation_failed operation=deleteById commentId={} error={} requestId={}",
                    commentId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToDeleteCommentException(e);
        }
    }
}
