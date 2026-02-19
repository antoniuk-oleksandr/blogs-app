package com.example.blogs.app.api.user.dto;

public record UpdateUserRequestDTO(
        String email,
        String bio,
        String password,
        String username
) {
}
