package com.example.blogs.app.api.search.dto;

public record SearchPostAuthor(
        Long id,
        String username,
        String firstName,
        String surname,
        String profilePictureUrl
) {
}
