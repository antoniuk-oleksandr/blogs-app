package com.example.blogs.app.api.user.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDTO(
        String username,
        String bio,
        String profilePictureUrl,
        List<UserPostSummaryDto> posts
) {
}
