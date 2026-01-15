package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;

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
}
