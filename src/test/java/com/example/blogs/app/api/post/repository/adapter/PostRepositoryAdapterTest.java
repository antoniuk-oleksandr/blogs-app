package com.example.blogs.app.api.post.repository.adapter;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.exception.*;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.repository.PostRepository;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostRepositoryAdapterTest {

    @Mock
    private PostRepository postRepository;

    private PostRepositoryAdapter postRepositoryAdapter;

    @BeforeEach
    void setUp() {
        postRepositoryAdapter = new PostRepositoryAdapterImpl(postRepository);
    }

    @Test
    void findByAuthorId_shouldReturnPosts_whenAuthorIdExists() {
        List<PostEntity> posts = List.of(new PostEntity(), new PostEntity());
        when(postRepository.findByAuthorId(anyLong())).thenReturn(posts);

        List<PostEntity> result = postRepositoryAdapter.findByAuthorId(1L);

        assertThat(result).isEqualTo(posts);
        verify(postRepository).findByAuthorId(1L);
    }

    @Test
    void findByAuthorId_shouldThrowException_whenRepositoryFails() {
        when(postRepository.findByAuthorId(anyLong())).thenThrow(FailedToFindPostsByAuthorIdException.class);

        assertThatThrownBy(() -> postRepositoryAdapter.findByAuthorId(1L))
                .isInstanceOf(RuntimeException.class);
        verify(postRepository).findByAuthorId(1L);
    }

    @Test
    void findAllByIdIn_shouldReturnPosts_whenRepositorySucceeds() {
        List<Long> postIds = List.of(1L, 2L);
        List<PostEntity> posts = List.of(new PostEntity(), new PostEntity());
        when(postRepository.findAllByIdIn(postIds)).thenReturn(posts);

        List<PostEntity> result = postRepositoryAdapter.findAllByIdIn(postIds);

        assertThat(result).isEqualTo(posts);
        verify(postRepository).findAllByIdIn(postIds);
    }

    @Test
    void findAllByIdIn_shouldThrowFailedToFindPostByIdException_whenRepositoryFails() {
        List<Long> postIds = List.of(1L, 2L);
        when(postRepository.findAllByIdIn(postIds)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> postRepositoryAdapter.findAllByIdIn(postIds))
                .isInstanceOf(FailedToFindPostByIdException.class)
                .hasMessage("Failed to find post by ID");
        verify(postRepository).findAllByIdIn(postIds);
    }

    @Test
    void deleteById_shouldDeletePost_whenPostIdExists() {
        Long postId = 1L;
        when(postRepository.deleteByIdReturningCount(postId)).thenReturn(postId);

        postRepositoryAdapter.deleteById(postId);

        verify(postRepository).deleteByIdReturningCount(postId);
    }

    @Test
    void deleteById_shouldThrowPostNotFound_whenPostIdDoesNotExist() {
        Long postId = 1L;
        when(postRepository.deleteByIdReturningCount(postId)).thenReturn(null);

        assertThatThrownBy(() -> postRepositoryAdapter.deleteById(postId))
                .isInstanceOf(PostNotFoundException.class);
        verify(postRepository).deleteByIdReturningCount(postId);
    }

    @Test
    void deleteById_shouldThrowPostNotFound_whenReturnedIdIsDifferent() {
        Long postId = 1L;
        when(postRepository.deleteByIdReturningCount(postId)).thenReturn(2L);

        assertThatThrownBy(() -> postRepositoryAdapter.deleteById(postId))
                .isInstanceOf(PostNotFoundException.class);
        verify(postRepository).deleteByIdReturningCount(postId);
    }

    @Test
    void deleteById_shouldThrowFailedToDeletePostException_whenRepositoryFails() {
        Long postId = 1L;
        when(postRepository.deleteByIdReturningCount(postId)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> postRepositoryAdapter.deleteById(postId))
                .isInstanceOf(FailedToDeletePostException.class);
        verify(postRepository).deleteByIdReturningCount(postId);
    }

    @Test
    void findBySlug_shouldReturnPost_whenSlugExists() {
        PostEntity post = new PostEntity();
        when(postRepository.findBySlug(anyString())).thenReturn(Optional.of(post));

        PostEntity result = postRepositoryAdapter.findBySlug("slug");

        assertThat(result).isEqualTo(post);
        verify(postRepository).findBySlug("slug");
    }

    @Test
    void findBySlug_shouldThrowPostNotFound_whenSlugDoesNotExist() {
        when(postRepository.findBySlug(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postRepositoryAdapter.findBySlug("slug"))
                .isInstanceOf(PostNotFoundException.class);
        verify(postRepository).findBySlug("slug");
    }

    @Test
    void findBySlug_shouldThrowFailedToFindPostBySlugException_whenRepositoryFails() {
        when(postRepository.findBySlug(anyString())).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> postRepositoryAdapter.findBySlug("slug"))
                .isInstanceOf(FailedToFindPostBySlugException.class);
        verify(postRepository).findBySlug("slug");
    }

    @Test
    void existsById_shouldReturnTrue_whenPostIdExists() {
        when(postRepository.existsById(anyLong())).thenReturn(true);

        boolean result = postRepositoryAdapter.existsById(1L);

        assertThat(result).isTrue();
        verify(postRepository).existsById(1L);
    }

    @Test
    void existsById_shouldReturnFalse_whenPostIdDoesNotExist() {
        when(postRepository.existsById(anyLong())).thenReturn(false);

        boolean result = postRepositoryAdapter.existsById(1L);

        assertThat(result).isFalse();
        verify(postRepository).existsById(1L);
    }

    @Test
    void existsByIdAndAuthorId_shouldReturnTrue_whenPostIdAndAuthorIdExist() {
        when(postRepository.existsByIdAndAuthorId(anyLong(), anyLong())).thenReturn(true);

        boolean result = postRepositoryAdapter.existsByIdAndAuthorId(1L, 1L);

        assertThat(result).isTrue();
        verify(postRepository).existsByIdAndAuthorId(1L, 1L);
    }

    @Test
    void existsByIdAndAuthorId_shouldReturnFalse_whenPostIdAndAuthorIdDoNotExist() {
        when(postRepository.existsByIdAndAuthorId(anyLong(), anyLong())).thenReturn(false);

        boolean result = postRepositoryAdapter.existsByIdAndAuthorId(1L, 1L);

        assertThat(result).isFalse();
        verify(postRepository).existsByIdAndAuthorId(1L, 1L);
    }

    @Test
    void findById_shouldReturnPost_whenPostIdExists() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, author);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostEntity result = postRepositoryAdapter.findById(1L);

        assertThat(result).isEqualTo(post);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowPostNotFound_whenPostIdDoesNotExist() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postRepositoryAdapter.findById(1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found");
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowFailedToFindPostByIdException_whenRepositoryFails() {
        when(postRepository.findById(anyLong())).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> postRepositoryAdapter.findById(1L))
                .isInstanceOf(FailedToFindPostByIdException.class)
                .hasMessage("Failed to find post by ID");
        verify(postRepository).findById(1L);
    }

    @Test
    void update_shouldReturnUpdatedPost_whenRepositorySucceeds() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, author);

        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostEntity result = postRepositoryAdapter.update(post);

        assertThat(result).isEqualTo(post);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void update_shouldThrowFailedToUpdatePostException_whenRepositoryFails() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, author);

        when(postRepository.save(any(PostEntity.class))).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> postRepositoryAdapter.update(post))
                .isInstanceOf(FailedToUpdatePostException.class)
                .hasMessage("Failed to update post");
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void save_shouldReturnSavedPost_whenRepositorySucceeds() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, author);

        when(postRepository.save(any(PostEntity.class))).thenReturn(post);

        PostEntity result = postRepositoryAdapter.save(post);

        assertThat(result).isEqualTo(post);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void save_shouldThrowFailedToSavePostException_whenRepositoryFails() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, author);

        when(postRepository.save(any(PostEntity.class))).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> postRepositoryAdapter.save(post))
                .isInstanceOf(FailedToSavePostException.class)
                .hasMessage("Failed to save post");
        verify(postRepository, times(1)).save(post);
    }
}
