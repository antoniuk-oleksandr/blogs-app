package com.example.blogs.app.api.reaction.dto;

import com.example.blogs.app.api.reaction.entity.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Data transfer object for setting or updating a reaction on a post.
 */
public record ReactionSetRequestDTO(

        @NotNull(message = "Reaction type must not be null")
        @Schema(description = "Type of the reaction", example = "LIKE")
        ReactionType reactionType
) {
}
