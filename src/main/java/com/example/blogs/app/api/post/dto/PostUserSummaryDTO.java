package com.example.blogs.app.api.post.dto;

import lombok.Builder;

@Builder
public record PostUserSummaryDTO(
        Long id,
        String username,
        String profilePictureUrl
) {
}
