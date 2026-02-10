package com.example.blogs.app.api.comment.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object representing a comment.
 */
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
