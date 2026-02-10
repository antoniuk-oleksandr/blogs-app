package com.example.blogs.app.api.comment.service;

import com.example.blogs.app.api.comment.dto.CommentCreateRequestDTO;
import com.example.blogs.app.api.comment.dto.CommentDTO;
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

    /**
     * Creates a new comment on a specific post.
     * Validates post and user existence, saves the comment, and returns the created comment.
     *
     * @param postId the ID of the post to comment on
     * @param userId the ID of the user creating the comment
     * @param requestDTO request containing the comment content
     * @return newly created comment as DTO
     */
    CommentDTO createComment(Long postId, Long userId, CommentCreateRequestDTO requestDTO);
}
