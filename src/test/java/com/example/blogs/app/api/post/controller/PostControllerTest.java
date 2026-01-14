package com.example.blogs.app.api.post.controller;

import com.example.blogs.app.api.post.exception.FailedToDeletePostException;
import com.example.blogs.app.api.post.exception.PostNotFound;
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
        doThrow(new PostNotFound(null)).when(postService).deletePostById(1L);

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
}
