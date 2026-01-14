package com.example.blogs.app.util;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;

import java.time.LocalDateTime;

public class TestUtils {

    public static CommentEntity createMockCommentEntity(
            Long id, LocalDateTime time, UserEntity author, PostEntity post
    ) {
        return CommentEntity.builder()
                .id(id)
                .content("content")
                .createdAt(time)
                .updatedAt(time)
                .post(post)
                .author(author)
                .isEdited(false)
                .build();
    }

    public static UserEntity createMockUserEntity(Long id, LocalDateTime time) {
        return UserEntity.builder()
                .id(id)
                .passwordHash("passwordHash")
                .email("email@gmail.com")
                .bio("bio")
                .profilePictureUrl("profilePictureUrl")
                .createdAt(time)
                .updatedAt(time)
                .username("username")
                .build();
    }

    public static PostEntity createMockPostEntity(Long id, LocalDateTime time, UserEntity author) {
        return PostEntity.builder()
                .id(id)
                .title("title")
                .description("description")
                .slug("slug")
                .previewImageUrl("previewImageUrl")
                .content("content")
                .createdAt(time)
                .updatedAt(time)
                .author(author)
                .build();
    }
}
