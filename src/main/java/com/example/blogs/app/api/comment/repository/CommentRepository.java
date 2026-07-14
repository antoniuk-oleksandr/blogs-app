package com.example.blogs.app.api.comment.repository;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for managing comment persistence operations.
 */
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    /**
     * Retrieves all comments associated with a specific post.
     *
     * @param postId the ID of the post
     * @return list of comments for the post
     */
    List<CommentEntity> findAllByPostId(Long postId);

    long countByPostId(Long postId);

    /**
     * Checks if a comment exists with the specified ID and author ID.
     *
     * @param commentId the ID of the comment
     * @param authorId  the ID of the author
     * @return true if a comment with the given ID and author exists, false otherwise
     */
    boolean existsByIdAndAuthorId(Long commentId, Long authorId);

    Optional<CommentEntity> findById(Long commentId);
}
