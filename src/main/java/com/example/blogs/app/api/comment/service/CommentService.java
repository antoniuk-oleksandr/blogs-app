package com.example.blogs.app.api.comment.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;

import java.util.List;

public interface CommentService {
    List<CommentEntity> getCommentsByPostId(Long postId);
}
