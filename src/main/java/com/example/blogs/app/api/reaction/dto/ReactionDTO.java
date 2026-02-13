package com.example.blogs.app.api.reaction.dto;

import com.example.blogs.app.api.reaction.entity.ReactionType;

/**
 * Data transfer object representing a reaction on a post.
 */
public record ReactionDTO(
        Long id,
        ReactionType reactionType,
        Long userId,
        Long postId
) {
}
