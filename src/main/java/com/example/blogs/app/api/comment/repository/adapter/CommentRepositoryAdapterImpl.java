package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.exception.FailedToFindCommentsByPostIdException;
import com.example.blogs.app.api.comment.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Wraps comment repository operations with exception translation for consistent error handling.
 */
@Component
@AllArgsConstructor
public class CommentRepositoryAdapterImpl implements CommentRepositoryAdapter {

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
            throw new FailedToFindCommentsByPostIdException(e);
        }
    }
}
