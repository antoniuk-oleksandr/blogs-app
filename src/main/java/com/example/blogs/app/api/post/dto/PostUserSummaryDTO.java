package com.example.blogs.app.api.post.dto;

import lombok.Builder;

/**
 * Data transfer object representing a summary of a user associated with a post or comment.
 */
@Builder
public record PostUserSummaryDTO(
        Long id,
        String username,
        String profilePictureUrl
) {
}
