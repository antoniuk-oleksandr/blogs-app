package com.example.blogs.app.api.post.repository;

import com.example.blogs.app.api.post.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
