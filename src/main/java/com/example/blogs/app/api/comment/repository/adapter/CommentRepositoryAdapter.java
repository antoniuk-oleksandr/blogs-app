package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.exception.*;

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
     * Counts comments associated with a specific post with exception translation.
     *
     * @param postId the ID of the post
     * @return number of comments for the post
     */
    long countByPostId(Long postId);

    /**
     * Saves a comment entity to the database with exception translation.
     *
     * @param commentEntity the comment entity to save
     * @return the saved comment entity with generated ID
     * @throws FailedToCreateCommentException if the save operation fails
     */
    CommentEntity save(CommentEntity commentEntity);

    /**
     * Checks if a comment exists with the specified ID.
     *
     * @param commentId the ID of the comment
     * @return true if a comment with the given ID exists, false otherwise
     * @throws FailedToCheckCommentExistenceException if the check operation fails
     */
    boolean existsById(Long commentId);

    /**
     * Checks if a comment exists with the specified ID and author ID.
     *
     * @param commentId the ID of the comment
     * @param authorId  the ID of the author
     * @return true if a comment with the given ID and author exists, false otherwise
     * @throws FailedToCheckCommentExistenceException if the check operation fails
     */
    boolean existsByIdAndAuthorId(Long commentId, Long authorId);

    /**
     * Deletes a comment by its ID with exception translation.
     *
     * @param commentId the ID of the comment to delete
     * @throws FailedToDeleteCommentException if the delete operation fails
     */
    void deleteById(Long commentId);

    /**
     * Retrieves a comment by its ID with exception translation.
     *
     * @param commentId the ID of the comment to retrieve
     * @return the comment entity if found
     * @throws CommentNotFoundException if the comment does not exist
     * @throws FailedToFindCommentByIdException if the retrieval operation fails
     */
    CommentEntity findById(Long commentId);
}
