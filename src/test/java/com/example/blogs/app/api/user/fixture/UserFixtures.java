package com.example.blogs.app.api.user.fixture;

import com.example.blogs.app.api.user.entity.UserEntity;

import java.time.LocalDateTime;

/**
 * Test fixture factory for creating user entities with predefined values.
 */
public class UserFixtures {

    /**
     * Creates a user entity with default values for all fields.
     *
     * @return configured user entity
     */
    public static UserEntity user() {
        return UserEntity.builder()
                .username("username")
                .email("email@gmail.com")
                .passwordHash("passwordHash")
                .bio("bio")
                .profilePictureUrl("profilePictureUrl")
                .build();
    }

    /**
     * Creates a user entity with the specified ID.
     *
     * @param id the user ID
     * @return configured user entity
     */
    public static UserEntity user(Long id) {
        return user(id, LocalDateTime.now());
    }

    /**
     * Creates a user entity with the specified ID and timestamp.
     *
     * @param id the user ID
     * @param time the creation and update timestamp
     * @return configured user entity
     */
    public static UserEntity user(Long id, LocalDateTime time) {
        return UserEntity.builder()
                .id(id)
                .username("username")
                .email("email@gmail.com")
                .passwordHash("passwordHash")
                .bio("bio")
                .profilePictureUrl("profilePictureUrl")
                .createdAt(time)
                .updatedAt(time)
                .build();
    }

    /**
     * Creates a user entity with custom ID, username, and email.
     *
     * @param id the user ID
     * @param username the username
     * @param email the email address
     * @return configured user entity
     */
    public static UserEntity customUser(Long id, String username, String email) {
        return UserEntity.builder()
                .id(id)
                .username(username)
                .email(email)
                .passwordHash("passwordHash")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
