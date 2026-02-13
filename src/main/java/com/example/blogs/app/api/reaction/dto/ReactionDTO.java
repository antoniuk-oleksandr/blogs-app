package com.example.blogs.app.api.reaction.dto;

import com.example.blogs.app.api.reaction.entity.ReactionType;

public record ReactionDTO(
        Long id,
        ReactionType reactionType,
        Long userId,
        Long postId
) {
}
