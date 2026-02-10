package com.example.blogs.app.api.comment.service;

import com.example.blogs.app.api.comment.dto.CommentCreateRequestDTO;
import com.example.blogs.app.api.comment.dto.CommentDTO;
import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.mapper.CommentMapper;
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

    @Mock
    private CommentMapper commentMapper;

    private CommentService commentService;


    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepositoryAdapter, commentMapper);
    }

    @Test
    void getCommentsByPostId_shouldReturnComments() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(1L, now);
        PostEntity mockPost = PostFixtures.post(1L, now, mockUser);
        List<CommentEntity> mockComments = List.of(
                CommentFixtures.commentEntity(1L, now, mockUser, mockPost),
                CommentFixtures.commentEntity(2L, now, mockUser, mockPost)
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

    @Test
    void createComment_shouldCreateAndReturnCommentDTO() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Long commentId = 1L;
        Long postId = 1L;
        Long userId = 1L;
        String content = "content";
        UserEntity mockUser = UserFixtures.user(userId, now);
        PostEntity mockPost = PostFixtures.post(postId, now, mockUser);
        CommentEntity mockCommentEntity = CommentFixtures.commentEntity(commentId, now, mockUser, mockPost);
        CommentCreateRequestDTO requestDTO = new CommentCreateRequestDTO(content);
        CommentDTO mockCommentDTO = CommentFixtures.commentDTO(commentId, userId, postId, now);

        when(commentRepositoryAdapter.save(any(CommentEntity.class)))
                .thenReturn(mockCommentEntity);
        when(commentMapper.toCommentDTO(any(CommentEntity.class), anyLong(), anyLong()))
                .thenReturn(mockCommentDTO);

        CommentDTO actualDTO = commentService.createComment(postId, userId, requestDTO);

        assertThat(actualDTO).isEqualTo(mockCommentDTO);
        verify(commentRepositoryAdapter).save(any(CommentEntity.class));
        verify(commentMapper).toCommentDTO(mockCommentEntity, postId, userId);
    }
}
