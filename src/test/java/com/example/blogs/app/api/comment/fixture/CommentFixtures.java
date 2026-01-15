package com.example.blogs.app.api.comment.fixture;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;

import java.time.LocalDateTime;

public class CommentFixtures {

    public static CommentEntity comment(
            Long id, LocalDateTime time, UserEntity author, PostEntity post
    ) {
        return CommentEntity.builder()
                .id(id)
                .content("content")
                .createdAt(time)
                .updatedAt(time)
                .post(post)
                .author(author)
                .edited(false)
                .build();
    }
}
