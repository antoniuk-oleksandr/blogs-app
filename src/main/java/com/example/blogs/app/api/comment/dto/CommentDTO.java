package com.example.blogs.app.api.comment.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Data transfer object representing a comment.
 */
@Builder
public record CommentDTO(
        Long id,
        String content,
        Long postId,
        Long authorId,
        LocalDateTime updatedAt,
        LocalDateTime createdAt,
        boolean edited
) {
}
