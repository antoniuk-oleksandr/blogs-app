package com.example.blogs.app.api.comment.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.repository.adapter.CommentRepositoryAdapter;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepositoryAdapter commentRepositoryAdapter;

    private CommentService commentService;


    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepositoryAdapter);
    }

    @Test
    void getCommentsByPostId_shouldReturnComments() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(1L, now);
        PostEntity mockPost = PostFixtures.post(1L, now, mockUser);
        List<CommentEntity> mockComments = List.of(
                CommentFixtures.comment(1L, now, mockUser, mockPost),
                CommentFixtures.comment(2L, now, mockUser, mockPost)
        );
        when(commentRepositoryAdapter.findAllByPostId(1L)).thenReturn(mockComments);

        List<CommentEntity> actualComments = commentService.getCommentsByPostId(1L);

        assertThat(actualComments).isEqualTo(mockComments);
        verify(commentRepositoryAdapter).findAllByPostId(1L);
    }

    @Test
    void getCommentsByPostId_shouldReturnEmptyListWhenNoComments() {
        when(commentRepositoryAdapter.findAllByPostId(2L)).thenReturn(List.of());

        List<CommentEntity> actualComments = commentService.getCommentsByPostId(2L);

        assertThat(actualComments).isEmpty();
        verify(commentRepositoryAdapter).findAllByPostId(2L);
    }
}
