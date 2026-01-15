package com.example.blogs.app.api.post.controller;

import com.example.blogs.app.api.post.dto.PostCommentSummaryDTO;
import com.example.blogs.app.api.post.dto.PostDTO;
import com.example.blogs.app.api.post.dto.PostUserSummaryDTO;
import com.example.blogs.app.api.post.exception.FailedToDeletePostException;
import com.example.blogs.app.api.post.exception.FailedToFindPostBySlugException;
import com.example.blogs.app.api.post.exception.PostNotFoundException;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.service.PostService;
import com.example.blogs.app.exception.ExceptionHttpStatusMapper;
import com.example.blogs.app.exception.GlobalExceptionHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PostController.class)
@Import({GlobalExceptionHandler.class, ExceptionHttpStatusMapper.class})
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    PostService postService;

    @Test
    @SneakyThrows
    void deletePostById_shouldDeletePost() {
        mockMvc.perform(delete("/posts/{postId}", 1L))
                .andExpect(status().isNoContent());

        verify(postService).deletePostById(1L);
    }

    @Test
    @SneakyThrows
    void deletePostById_shouldReturn404_whenServiceThrowsPostNotFoundException() {
        doThrow(new PostNotFoundException(null)).when(postService).deletePostById(1L);

        mockMvc.perform(delete("/posts/{postId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found"));

        verify(postService).deletePostById(1L);
    }

    @Test
    @SneakyThrows
    void deletePostById_shouldReturn500_whenServiceThrowsFaildToDeletePostException() {
        doThrow(new FailedToDeletePostException(null)).when(postService).deletePostById(1L);

        mockMvc.perform(delete("/posts/{postId}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to delete post"));

        verify(postService).deletePostById(1L);
    }

    @Test
    @SneakyThrows
    void deletePostById_shouldReturn500_whenServiceThrowsUnexpectedException() {
        doThrow(new RuntimeException("Unexpected error")).when(postService).deletePostById(1L);

        mockMvc.perform(delete("/posts/{postId}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"));

        verify(postService).deletePostById(1L);
    }

    @Test
    @SneakyThrows
    void getPostBySlug_shouldReturn200_whenPostExists() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        PostUserSummaryDTO mockAuthor = PostFixtures.postUserSummaryDTO(1L);
        List<PostCommentSummaryDTO> mockComments = List.of(
                PostFixtures.postCommentSummaryDTO(1L, now, mockAuthor)
        );
        PostDTO mockPostDTO = PostFixtures.postDTO(1L, now, mockAuthor, mockComments);
        when(postService.getPostBySlug(anyString())).thenReturn(mockPostDTO);

        mockMvc.perform(get("/posts/{slug}", "slug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.slug").value("slug"))
                .andExpect(jsonPath("$.author.id").value(1L))
                .andExpect(jsonPath("$.author.username").value("username"))
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments.length()").value(1))
                .andExpect(jsonPath("$.comments[0].id").value(1L));


        verify(postService).getPostBySlug("slug");
    }

    @Test
    @SneakyThrows
    void getPostBySlug_shouldReturn404_whenPostDoesNotExist() {
        when(postService.getPostBySlug(anyString()))
                .thenThrow(new PostNotFoundException(null));

        mockMvc.perform(get("/posts/{slug}", "non-existent-slug"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found"));

        verify(postService).getPostBySlug("non-existent-slug");
    }

    @Test
    @SneakyThrows
    void getPostBySlug_shouldReturn500_whenServiceThrowsFailedToFindPostBySlugException() {
        when(postService.getPostBySlug(anyString()))
                .thenThrow(new FailedToFindPostBySlugException(null));
        mockMvc.perform(get("/posts/{slug}", "slug"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Failed to find post by slug"));

        verify(postService).getPostBySlug("slug");
    }

    @Test
    @SneakyThrows
    void getPostBySlug_shouldReturn500_whenServiceThrowsUnexpectedException() {
        when(postService.getPostBySlug(anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));
        mockMvc.perform(get("/posts/{slug}", "slug"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Unexpected error"));

        verify(postService).getPostBySlug("slug");
    }
}
