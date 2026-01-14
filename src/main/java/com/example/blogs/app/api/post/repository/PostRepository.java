package com.example.blogs.app.api.post.repository;

import com.example.blogs.app.api.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * JPA repository for managing post persistence operations.
 */
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    /**
     * Retrieves all posts authored by the specified user.
     *
     * @param userId the ID of the author
     * @return list of posts by the author
     */
    List<PostEntity> findByAuthorId(long userId);

    /**
     * Deletes a post by its ID and returns the deleted post's ID.
     *
     * @param postId the ID of the post to delete
     * @return the ID of the deleted post, or null if no post was found
     */
    @Query(value = "DELETE FROM posts WHERE id = :id RETURNING id", nativeQuery = true)
    Long deleteByIdReturningCount(@Param("id") Long postId);
}
