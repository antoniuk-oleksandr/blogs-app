package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.exception.FailedToDeletePostException;
import com.example.blogs.app.api.post.exception.FailedToFindPostBySlugException;
import com.example.blogs.app.api.post.exception.FailedToFindPostsByAuthorIdException;
import com.example.blogs.app.api.post.exception.PostNotFoundException;
import com.example.blogs.app.api.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Wraps post repository operations with exception translation for consistent error handling.
 */
@Component
@AllArgsConstructor
public class PostRepositoryAdapterImpl implements PostRepositoryAdapter {

    private final PostRepository postRepository;

    /**
     * Retrieves all posts authored by the specified user with exception translation.
     * Wraps repository exceptions in a domain-specific exception for consistent error handling.
     *
     * @param userId the ID of the author
     * @return list of posts by the author
     * @throws FailedToFindPostsByAuthorIdException if the repository operation fails
     */
    @Override
    public List<PostEntity> findByAuthorId(long userId) {
        try {
            return postRepository.findByAuthorId(userId);
        } catch (Exception e) {
            throw new FailedToFindPostsByAuthorIdException(e);
        }
    }

    /**
     * Deletes a post by its ID with exception translation.
     * Verifies deletion success and wraps repository exceptions in domain-specific exceptions.
     *
     * @param postId the ID of the post to delete
     * @throws PostNotFoundException       if the post does not exist
     * @throws FailedToDeletePostException if the repository operation fails
     */
    @Override
    public void deleteById(Long postId) {
        try {
            Long id = postRepository.deleteByIdReturningCount(postId);
            if (id == null || !id.equals(postId)) {
                throw new PostNotFoundException(null);
            }
        } catch (PostNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToDeletePostException(e);
        }
    }

    /**
     * Retrieves a post by its unique slug identifier with exception translation.
     * Wraps repository exceptions in domain-specific exceptions for consistent error handling.
     *
     * @param slug the unique slug of the post
     * @return the post entity
     * @throws PostNotFoundException if the post does not exist
     * @throws FailedToFindPostBySlugException if the repository operation fails
     */
    @Override
    public PostEntity findBySlug(String slug) {
        try {
            return postRepository.findBySlug(slug)
                    .orElseThrow(() -> new PostNotFoundException(null));
        } catch (PostNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToFindPostBySlugException(e);
        }
    }
}
