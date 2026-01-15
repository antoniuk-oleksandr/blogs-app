package com.example.blogs.app.api.user.fixture;

import com.example.blogs.app.api.user.entity.UserEntity;

import java.time.LocalDateTime;

public class UserFixtures {

    public static UserEntity user() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        return user(null, now);
    }

    public static UserEntity user(Long id) {
        return user(id, LocalDateTime.now());
    }

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
