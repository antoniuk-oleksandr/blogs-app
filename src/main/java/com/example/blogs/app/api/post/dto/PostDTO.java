package com.example.blogs.app.api.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDTO(
        Long id,
        String title,
        String content,
        String slug,
        String previewImageUrl,
        LocalDateTime createdAt,
        PostUserSummaryDTO author,
        List<PostCommentSummaryDTO> comments
) {
}
