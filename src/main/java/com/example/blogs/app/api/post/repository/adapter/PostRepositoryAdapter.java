package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.exception.*;

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
     * Retrieves posts by IDs with author and file data loaded for search indexing.
     *
     * @param postIds the IDs of posts to retrieve
     * @return matching posts
     */
    List<PostEntity> findAllByIdIn(List<Long> postIds);

    /**
     * Deletes a post by its ID with exception translation.
     *
     * @param postId the ID of the post to delete
     * @throws PostNotFoundException       if the post does not exist
     * @throws FailedToDeletePostException if the repository operation fails
     */
    void deleteById(Long postId);

    /**
     * Retrieves a post by its unique slug identifier with exception translation.
     *
     * @param slug the unique slug of the post
     * @return the post entity
     * @throws PostNotFoundException if the post does not exist
     */
    PostEntity findBySlug(String slug);

    /**
     * Checks if a post exists by its ID.
     *
     * @param postId the ID of the post
     * @return true if the post exists, false otherwise
     */
    boolean existsById(long postId);

    /**
     * Checks if a post exists by its ID and author ID.
     *
     * @param postId   the ID of the author
     * @param authorId the ID of the author
     * @return true if the post exists and belongs to the author, false otherwise
     */
    boolean existsByIdAndAuthorId(long postId, long authorId);

    /**
     * Retrieves a post by its ID with exception translation.
     *
     * @param postId the ID of the post
     * @return the post entity
     * @throws PostNotFoundException         if the post does not exist
     * @throws FailedToFindPostByIdException if the repository operation fails
     */
    PostEntity findById(long postId);

    /**
     * Updates a post entity with exception translation.
     *
     * @param postEntity the post entity to update
     * @return the updated post entity
     * @throws FailedToUpdatePostException if the repository operation fails
     */
    PostEntity update(PostEntity postEntity);

    /**
     * Saves a new post entity with exception translation.
     *
     * @param postEntity the post entity to save
     * @return the saved post entity with generated ID
     * @throws FailedToSavePostException if the repository operation fails
     */
    PostEntity save(PostEntity postEntity);
}
