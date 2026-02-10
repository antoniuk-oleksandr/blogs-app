package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.exception.FailedToCreateCommentException;

import java.util.List;

/**
 * Adapter for comment repository operations with exception handling.
 */
public interface CommentRepositoryAdapter {

    /**
     * Retrieves all comments associated with a specific post with exception translation.
     *
     * @param postId the ID of the post
     * @return list of comments for the post
     */
    List<CommentEntity> findAllByPostId(Long postId);

    /**
     * Saves a comment entity to the database with exception translation.
     *
     * @param commentEntity the comment entity to save
     * @return the saved comment entity with generated ID
     * @throws FailedToCreateCommentException if the save operation fails
     */
    CommentEntity save(CommentEntity commentEntity);

    boolean existsById(Long commentId);

    boolean existsByIdAndAuthorId(Long commentId, Long authorId);

    void deleteById(Long commentId);
}
