package com.example.blogs.app.api.user.dto;

import lombok.Builder;

@Builder
public record UpdateUserResponseDTO(
        String username,
        String bio,
        String profilePictureUrl
) {
}
