package com.example.blogs.app.api.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Data transfer object representing a summary of a comment on a post.
 */
@Builder
public record PostCommentSummaryDTO(
        Long id,
        String content,
        PostUserSummaryDTO author,
        LocalDateTime updatedAt,
        LocalDateTime createdAt,
        boolean edited
) {
}
