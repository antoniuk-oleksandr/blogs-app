package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.post.entity.PostEntity;

import java.util.List;

public interface PostRepositoryAdapter {

    List<PostEntity> findByAuthorId(long userId);
}
