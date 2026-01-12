package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.entity.PostEntity;

import java.util.List;

public interface PostService {

    List<PostEntity> getPostsByUserId(long userId);
}
