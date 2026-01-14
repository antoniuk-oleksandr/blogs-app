package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.exception.FailedToFindCommentsByPostIdException;
import com.example.blogs.app.api.comment.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CommentRepositoryAdapterImpl implements CommentRepositoryAdapter {

    private final CommentRepository commentRepository;

    @Override
    public List<CommentEntity> findAllByPostId(Long postId) {
        try {
            return commentRepository.findAllByPostId(postId);
        } catch (Exception e) {
            throw new FailedToFindCommentsByPostIdException(e);
        }
    }
}
