package com.example.blogs.app.api.comment.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepositoryAdapter commentRepositoryAdapter;

    @Override
    public List<CommentEntity> getCommentsByPostId(Long postId) {
        return commentRepositoryAdapter.findAllByPostId(postId);
    }
}
