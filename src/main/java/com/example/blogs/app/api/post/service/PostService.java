package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.entity.PostEntity;

import java.util.List;

/**
 * Service for post-related business operations.
 */
public interface PostService {

    /**
     * Retrieves all posts created by the specified user.
     *
     * @param userId the ID of the user
     * @return list of posts created by the user
     */
    List<PostEntity> getPostsByUserId(long userId);
}
