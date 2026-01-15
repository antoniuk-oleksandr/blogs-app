package com.example.blogs.app.api.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCommentSummaryDTO(
        Long id,
        String content,
        PostUserSummaryDTO author,
        LocalDateTime createdAt,
        boolean edited
) {
}
