package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.exception.FailedToFindPostsByAuthorIdException;

import java.util.List;

/**
 * Adapter for post repository operations with exception handling.
 */
public interface PostRepositoryAdapter {

    /**
     * Retrieves all posts authored by the specified user with exception translation.
     *
     * @param userId the ID of the author
     * @return list of posts by the author
     * @throws FailedToFindPostsByAuthorIdException if the repository operation fails
     */
    List<PostEntity> findByAuthorId(long userId);

    /**
     * Deletes a post by its ID with exception translation.
     *
     * @param postId the ID of the post to delete
     * @throws PostNotFound if the post does not exist
     * @throws FailedToDeletePostException if the repository operation fails
     */
    void deleteById(Long postId);
}
