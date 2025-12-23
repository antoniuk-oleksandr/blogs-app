package com.example.blogs.app.api.user.dto;

/**
 * Command object for creating a new user with validated and processed data.
 *
 * @param username the unique username for the user
 * @param passwordHash the BCrypt-hashed password
 * @param email the unique email address
 */
public record CreateUserCommand(
        String username,
        String passwordHash,
        String email
) {
}
