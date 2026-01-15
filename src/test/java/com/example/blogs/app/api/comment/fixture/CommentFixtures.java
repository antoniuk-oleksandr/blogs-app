package com.example.blogs.app.api.comment.fixture;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;

import java.time.LocalDateTime;

/**
 * Test fixture factory for creating comment entities with predefined values.
 */
public class CommentFixtures {

    /**
     * Creates a comment entity with the specified attributes.
     *
     * @param id the comment ID
     * @param time the creation and update timestamp
     * @param author the comment author
     * @param post the post the comment belongs to
     * @return configured comment entity
     */
    public static CommentEntity comment(
            Long id, LocalDateTime time, UserEntity author, PostEntity post
    ) {
        return CommentEntity.builder()
                .id(id)
                .content("content")
                .createdAt(time)
                .updatedAt(time)
                .post(post)
                .author(author)
                .edited(false)
                .build();
    }
}
