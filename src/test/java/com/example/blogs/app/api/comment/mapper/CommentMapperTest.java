package com.example.blogs.app.api.comment.mapper;

import com.example.blogs.app.api.comment.dto.CommentDTO;
import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for comment mapper entity-to-DTO conversions.
 */
class CommentMapperTest {

    @Test
    void toCommentDTO_shouldMapAllFieldsCorrectly() {
        Long commentId = 1L;
        Long authorId = 1L;
        Long postId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(authorId, now);
        PostEntity post = PostFixtures.post(postId, now, author);
        CommentEntity commentEntity = CommentFixtures.commentEntity(commentId, now, author, post);

        CommentDTO result = new CommentMapperImpl().toCommentDTO(commentEntity, postId, authorId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(commentId);
        assertThat(result.content()).isEqualTo("content");
        assertThat(result.postId()).isEqualTo(postId);
        assertThat(result.authorId()).isEqualTo(authorId);
        assertThat(result.updatedAt()).isEqualTo(now);
        assertThat(result.createdAt()).isEqualTo(now);
        assertThat(result.edited()).isFalse();
    }

    @Test
    void toCommentDTO_shouldReturnNull_whenAllParametersAreNull() {
        CommentDTO result = new CommentMapperImpl().toCommentDTO(null, null, null);

        assertThat(result).isNull();
    }
}
