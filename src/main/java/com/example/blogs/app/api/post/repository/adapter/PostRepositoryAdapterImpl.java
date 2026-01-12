package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.exception.FailedToFindPostsByAuthorIdException;
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
}
