package com.example.blogs.app.api.comment.repository.adapter;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.exception.*;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.repository.CommentRepository;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
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
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        Long firstCommentId = 1L;
        Long secondCommentId = 2L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(fileId, now);
        UserEntity mockUser = UserFixtures.user(userId, mockFile, now);
        PostEntity mockPost = PostFixtures.post(postId, now, mockUser);
        List<CommentEntity> mockComments = List.of(
                CommentFixtures.commentEntity(firstCommentId, now, mockUser, mockPost),
                CommentFixtures.commentEntity(secondCommentId, now, mockUser, mockPost)
        );
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(mockComments);

        List<CommentEntity> actualComments = commentRepositoryAdapter.findAllByPostId(postId);

        assertThat(actualComments).isEqualTo(mockComments);
        verify(commentRepository).findAllByPostId(postId);
    }

    @Test
    void findAllByPostId_shouldReturnEmptyList_whenNoCommentsExist() {
        Long postId = 1L;
        when(commentRepository.findAllByPostId(anyLong()))
                .thenReturn(List.of());

        List<CommentEntity> actualComments = commentRepositoryAdapter.findAllByPostId(postId);

        assertThat(actualComments).isEqualTo(List.of());
        verify(commentRepository).findAllByPostId(postId);
    }

    @Test
    void findAllByPostId_shouldThrowFailedToFindCommentsByPostIdException_whenDBExceptionOccurs() {
        Long postId = 1L;

        when(commentRepository.findAllByPostId(anyLong()))
                .thenThrow(new RuntimeException("Db exception"));

        assertThatThrownBy(() -> commentRepositoryAdapter.findAllByPostId(postId))
                .isInstanceOf(FailedToFindCommentsByPostIdException.class);
        verify(commentRepository).findAllByPostId(postId);
    }

    @Test
    void save_shouldReturnSavedComment() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(fileId, now);
        UserEntity mockUser = UserFixtures.user(userId, mockFile, now);
        PostEntity mockPost = PostFixtures.post(postId, now, mockUser);
        CommentEntity mockComment = CommentFixtures.commentEntity(1L, now, mockUser, mockPost);

        when(commentRepository.save(any(CommentEntity.class))).thenReturn(mockComment);

        CommentEntity actualComment = commentRepositoryAdapter.save(mockComment);

        assertThat(actualComment).isEqualTo(mockComment);
        verify(commentRepository).save(mockComment);
    }

    @Test
    void save_shouldThrowException_whenDBExceptionOccurs() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        Long commentId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(fileId, now);
        UserEntity mockUser = UserFixtures.user(userId, mockFile, now);
        PostEntity mockPost = PostFixtures.post(postId, now, mockUser);
        CommentEntity mockComment = CommentFixtures.commentEntity(commentId, now, mockUser, mockPost);

        when(commentRepository.save(any(CommentEntity.class)))
                .thenThrow(new RuntimeException("Db exception"));

        assertThatThrownBy(() -> commentRepositoryAdapter.save(mockComment))
                .isInstanceOf(FailedToCreateCommentException.class)
                .hasMessageContaining("Failed to create comment");
        verify(commentRepository).save(mockComment);
    }

    @Test
    void deleteById_shouldDeleteComment() {
        commentRepositoryAdapter.deleteById(1L);

        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteById_shouldThrowFailedToDeleteCommentException_whenDBExceptionOccurs() {
        Long commentId = 1L;

        doThrow(new RuntimeException("Db exception"))
                .when(commentRepository).deleteById(anyLong());

        assertThatThrownBy(() -> commentRepositoryAdapter.deleteById(commentId))
                .isInstanceOf(FailedToDeleteCommentException.class)
                .hasMessageContaining("Failed to delete comment");
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void existsById_shouldReturnTrue_whenCommentExists() {
        Long commentId = 1L;

        when(commentRepository.existsById(anyLong()))
                .thenReturn(true);

        boolean exists = commentRepositoryAdapter.existsById(commentId);

        assertThat(exists).isTrue();
        verify(commentRepository).existsById(commentId);
    }

    @Test
    void existsById_shouldThrowFailedToCheckCommentExistenceException_whenDBExceptionOccurs() {
        Long commentId = 1L;

        when(commentRepository.existsById(anyLong()))
                .thenThrow(new RuntimeException("Db exception"));

        assertThatThrownBy(() -> commentRepositoryAdapter.existsById(commentId))
                .isInstanceOf(FailedToCheckCommentExistenceException.class)
                .hasMessageContaining("Failed to check comment existence");
        verify(commentRepository).existsById(commentId);
    }

    @Test
    void existsByIdAndAuthorId_shouldReturnTrue_whenCommentExists() {
        Long commentId = 1L;
        Long authorId = 1L;

        when(commentRepository.existsByIdAndAuthorId(anyLong(), anyLong()))
                .thenReturn(true);

        boolean exists = commentRepositoryAdapter.existsByIdAndAuthorId(commentId, authorId);

        assertThat(exists).isTrue();
        verify(commentRepository).existsByIdAndAuthorId(commentId, authorId);
    }

    @Test
    void existsByIdAndAuthorId_shouldThrowFailedToCheckCommentExistenceException_whenDBExceptionOccurs() {
        Long commentId = 1L;
        Long authorId = 1L;

        when(commentRepository.existsByIdAndAuthorId(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("Db exception"));

        assertThatThrownBy(() -> commentRepositoryAdapter.existsByIdAndAuthorId(commentId, authorId))
                .isInstanceOf(FailedToCheckCommentExistenceException.class)
                .hasMessageContaining("Failed to check comment existence");
        verify(commentRepository).existsByIdAndAuthorId(commentId, authorId);
    }

    @Test
    void findBydId_shouldReturnComment_whenCommentExists() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        Long commentId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(fileId, now);
        UserEntity mockUser = UserFixtures.user(userId, mockFile, now);
        PostEntity mockPost = PostFixtures.post(postId, now, mockUser);
        CommentEntity mockComment = CommentFixtures.commentEntity(commentId, now, mockUser, mockPost);

        when(commentRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mockComment));

        CommentEntity actualComment = commentRepositoryAdapter.findById(commentId);

        assertThat(actualComment).isEqualTo(mockComment);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void findById_shouldThrowCommentNotFoundException_whenCommentDoesNotExist() {
        Long commentId = 1L;

        when(commentRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> commentRepositoryAdapter.findById(commentId))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessage("Comment not found");
        verify(commentRepository).findById(commentId);
    }

    @Test
    void findBydId_shouldThrowFailedToFindCommentByIdException_whenDBExceptionOccurs() {
        Long commentId = 1L;

        when(commentRepository.findById(anyLong()))
                .thenThrow(new RuntimeException("Db exception"));

        assertThatThrownBy(() -> commentRepositoryAdapter.findById(commentId))
                .isInstanceOf(FailedToFindCommentByIdException.class)
                .hasMessage("Failed to find comment by ID");
        verify(commentRepository).findById(commentId);
    }
}
