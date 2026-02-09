package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.exception.*;
import com.example.blogs.app.api.post.repository.PostRepository;
import com.example.blogs.app.logging.MDCKeys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Wraps post repository operations with exception translation for consistent error handling.
 */
@Component
@AllArgsConstructor
public class PostRepositoryAdapterImpl implements PostRepositoryAdapter {

    private static final Logger log = LoggerFactory.getLogger(PostRepositoryAdapterImpl.class);

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
            log.error("database_operation_failed operation=findByAuthorId authorId={} error={} requestId={}",
                    userId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
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
            log.error("database_operation_failed operation=deleteById postId={} error={} requestId={}",
                    postId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToDeletePostException(e);
        }
    }

    /**
     * Retrieves a post by its unique slug identifier with exception translation.
     * Wraps repository exceptions in domain-specific exceptions for consistent error handling.
     *
     * @param slug the unique slug of the post
     * @return the post entity
     * @throws PostNotFoundException           if the post does not exist
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
            log.error("database_operation_failed operation=findBySlug slug={} error={} requestId={}",
                    slug, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToFindPostBySlugException(e);
        }
    }

    /**
     * Checks if a post exists by its ID.
     *
     * @param postId the ID of the post
     * @return true if the post exists, false otherwise
     */
    @Override
    public boolean existsById(long postId) {
        return postRepository.existsById(postId);
    }

    /**
     * Checks if a post exists by its ID and author ID.
     *
     * @param postId   the ID of the post
     * @param authorId the ID of the author
     * @return true if the post exists and belongs to the author, false otherwise
     */
    @Override
    public boolean existsByIdAndAuthorId(long postId, long authorId) {
        return postRepository.existsByIdAndAuthorId(postId, authorId);
    }

    /**
     * Retrieves a post by its ID with exception translation.
     * Wraps repository exceptions in domain-specific exceptions for consistent error handling.
     *
     * @param postId the ID of the post
     * @return the post entity
     * @throws PostNotFoundException         if the post does not exist
     * @throws FailedToFindPostByIdException if the repository operation fails
     */
    @Override
    public PostEntity findById(long postId) {
        try {
            return postRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException(null));
        } catch (PostNotFoundException e) {
            throw e;
        } catch (Exception e) { 
            log.error("database_operation_failed operation=findById postId={} error={} requestId={}",
                    postId, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToFindPostByIdException(e);
        }
    }

    /**
     * Updates a post entity with exception translation.
     * Wraps repository exceptions in domain-specific exceptions for consistent error handling.
     *
     * @param postEntity the post entity to update
     * @return the updated post entity
     * @throws FailedToUpdatePostException if the repository operation fails
     */
    @Override
    public PostEntity update(PostEntity postEntity) {
        try {
            return postRepository.save(postEntity);
        } catch (Exception e) { 
            log.error("database_operation_failed operation=update postId={} error={} requestId={}",
                    postEntity.getId(), e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToUpdatePostException(e);
        }
    }

    /**
     * Saves a new post entity with exception translation.
     * Wraps repository exceptions in domain-specific exceptions for consistent error handling.
     *
     * @param postEntity the post entity to save
     * @return the saved post entity with generated ID
     * @throws FailedToSavePostException if the repository operation fails
     */
    @Override
    public PostEntity save(PostEntity postEntity) {
        try {
            return postRepository.save(postEntity);
        } catch (Exception e) { 
            log.error("database_operation_failed operation=save title={} authorId={} error={} requestId={}",
                    postEntity.getTitle(), postEntity.getAuthor().getId(), e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToSavePostException(e);
        }
    }
}
