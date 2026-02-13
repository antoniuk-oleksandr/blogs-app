package com.example.blogs.app.api.reaction.fixture;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import com.example.blogs.app.api.reaction.entity.ReactionType;
import com.example.blogs.app.api.user.entity.UserEntity;

import java.time.LocalDateTime;

/**
 * Test fixture factory for creating reaction entities and DTOs with predefined values.
 */
public class ReactionFixtures {

    /**
     * Creates a reaction entity with the specified attributes.
     *
     * @param id           the reaction ID
     * @param time         the creation timestamp
     * @param user         the user who reacted
     * @param post         the post being reacted to
     * @param reactionType the type of reaction
     * @return configured reaction entity
     */
    public static ReactionEntity reactionEntity(
            Long id, LocalDateTime time, UserEntity user, PostEntity post, ReactionType reactionType
    ) {
        return ReactionEntity.builder()
                .id(id)
                .user(user)
                .post(post)
                .reactionType(reactionType)
                .createdAt(time)
                .build();
    }

    /**
     * Creates a reaction entity with the specified user, post, and reaction type.
     *
     * @param user         the user who reacted
     * @param post         the post being reacted to
     * @param reactionType the type of reaction
     * @return configured reaction entity
     */
    public static ReactionEntity reactionEntity(UserEntity user, PostEntity post, ReactionType reactionType) {
        return ReactionEntity.builder()
                .user(user)
                .post(post)
                .reactionType(reactionType)
                .build();
    }
}
