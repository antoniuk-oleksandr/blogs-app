package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.post.dto.PostUpdateRequestDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostSlugUpdaterTest {

    @Mock
    private SlugService slugService;

    private PostSlugUpdater postSlugUpdater;

    @BeforeEach
    void setUp() {
        postSlugUpdater = new PostSlugUpdater(slugService);
    }

    @Test
    void apply_shouldUpdatePostTitleAndSlug() {
        Long postId = 1L;
        String newSlug = "newSlug";
        UserEntity user = null;
        PostUpdateRequestDTO requestDTO = PostFixtures.postUpdateRequestDTO();
        LocalDateTime now = LocalDateTime.now().withNano(0);
        PostEntity post = PostFixtures.post(postId, now, user);

        when(slugService.generate(anyString())).thenReturn(newSlug);

        postSlugUpdater.apply(post, requestDTO);

        assertThat(post.getSlug()).isEqualTo(newSlug);
        verify(slugService).generate(requestDTO.title());
    }

    @Test
    void apply_shouldNotUpdateSlugWhenTitleIsNull() {
        Long postId = 1L;
        UserEntity user = null;
        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                null, "newDescription", "newContent"
        );
        LocalDateTime now = LocalDateTime.now().withNano(0);
        PostEntity post = PostFixtures.post(postId, now, user);
        String originalSlug = post.getSlug();

        postSlugUpdater.apply(post, requestDTO);

        assertThat(post.getSlug()).isEqualTo(originalSlug);
        verify(slugService, never()).generate(anyString());
    }

    @Test
    void apply_shouldNotUpdateSlugWhenTitleIsBlank() {
        Long postId = 1L;
        UserEntity user = null;
        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                "   ", "newDescription", "newContent"
        );
        LocalDateTime now = LocalDateTime.now().withNano(0);
        PostEntity post = PostFixtures.post(postId, now, user);
        String originalSlug = post.getSlug();

        postSlugUpdater.apply(post, requestDTO);

        assertThat(post.getSlug()).isEqualTo(originalSlug);
        verify(slugService, never()).generate(anyString());
    }
}
