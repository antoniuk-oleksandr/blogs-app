package com.example.blogs.app.api.comment.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates comment retrieval operations by coordinating with the comment repository adapter.
 */
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepositoryAdapter commentRepositoryAdapter;

    /**
     * Retrieves all comments associated with a specific post.
     * Delegates to the repository adapter for data retrieval.
     *
     * @param postId the ID of the post
     * @return list of comments for the post
     */
    @Override
    public List<CommentEntity> getCommentsByPostId(Long postId) {
        return commentRepositoryAdapter.findAllByPostId(postId);
    }
}
