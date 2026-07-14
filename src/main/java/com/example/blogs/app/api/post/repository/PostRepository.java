package com.example.blogs.app.api.post.repository;

import com.example.blogs.app.api.post.entity.PostEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    /**
     * Retrieves a post by its unique slug identifier.
     *
     * @param slug the unique slug of the post
     * @return optional containing the post if found, empty otherwise
     */
    Optional<PostEntity> findBySlug(String slug);

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
     * @param postId   the ID of the post
     * @param authorId the ID of the author
     * @return true if the post exists and belongs to the author, false otherwise
     */
    boolean existsByIdAndAuthorId(long postId, long authorId);

    /**
     * Retrieves a post by its ID.
     *
     * @param postId the ID of the post
     * @return optional containing the post if found, empty otherwise
     */
    Optional<PostEntity> findById(long postId);

    @EntityGraph(attributePaths = {"author", "author.file", "file"})
    List<PostEntity> findAllByIdIn(List<Long> postIds);
}
