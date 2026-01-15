package com.example.blogs.app.api.comment.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;

import java.util.List;

/**
 * Service for comment-related business operations.
 */
public interface CommentService {

    /**
     * Retrieves all comments associated with a specific post.
     *
     * @param postId the ID of the post
     * @return list of comments for the post
     */
    List<CommentEntity> getCommentsByPostId(Long postId);
}
