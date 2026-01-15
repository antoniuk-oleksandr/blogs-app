package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.exception.FailedToFindCommentsByPostIdException;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.repository.CommentRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentRepositoryAdapterTest {

    @Mock
    private CommentRepository commentRepository;

    private CommentRepositoryAdapter commentRepositoryAdapter;

    @BeforeEach
    void setUp() {
        commentRepositoryAdapter = new CommentRepositoryAdapterImpl(commentRepository);
    }

    @Test
    void findAllByPostId_shouldReturnAllPosts() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(1L, now);
        PostEntity mockPost = PostFixtures.post(1L, now, mockUser);
        List<CommentEntity> mockComments = List.of(
                CommentFixtures.comment(1L, now, mockUser, mockPost),
                CommentFixtures.comment(2L, now, mockUser, mockPost)
        );
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(mockComments);

        List<CommentEntity> actualComments = commentRepositoryAdapter.findAllByPostId(1L);

        assertThat(actualComments).isEqualTo(mockComments);
        verify(commentRepository).findAllByPostId(1L);
    }

    @Test
    void findAllByPostId_shouldReturnEmptyList_whenNoCommentsExist() {
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(List.of());

        List<CommentEntity> actualComments = commentRepositoryAdapter.findAllByPostId(1L);

        assertThat(actualComments).isEqualTo(List.of());
        verify(commentRepository).findAllByPostId(1L);
    }

    @Test
    void findAllByPostId_shouldThrowFailedToFindCommentsByPostIdException_whenDBExceptionOccurs() {
        when(commentRepository.findAllByPostId(anyLong()))
                .thenThrow(new RuntimeException("Db exception"));

        assertThatThrownBy(() -> commentRepositoryAdapter.findAllByPostId(1L))
                .isInstanceOf(FailedToFindCommentsByPostIdException.class);
    }
}
