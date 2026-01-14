package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;

import java.util.List;

public interface CommentRepositoryAdapter {

    List<CommentEntity> findAllByPostId(Long postId);
}
