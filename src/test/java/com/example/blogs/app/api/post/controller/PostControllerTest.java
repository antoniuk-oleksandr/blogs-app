package com.example.blogs.app.api.post.controller;

import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.exception.FailedToDeletePostException;
import com.example.blogs.app.api.post.exception.FailedToFindPostBySlugException;
import com.example.blogs.app.api.post.exception.FailedToUpdatePostException;
import com.example.blogs.app.api.post.exception.PostNotFoundException;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.service.PostService;
import com.example.blogs.app.exception.ErrorResponseWriter;
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
@Import({
        GlobalExceptionHandler.class,
        ExceptionHttpStatusMapper.class,
        ErrorResponseWriter.class
})
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

    @Test
    @SneakyThrows
    void updatePostById_shouldUpdatePost_whenAllFieldsAreProvided() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Long postId = 1L;
        String requestBody = """
                {
                    "title": "title",
                    "content": "content",
                    "description": "description",
                    "slug": "slug",
                    "previewImageUrl": "previewImageUrl"
                }
                """;

        PostUpdateResponseDTO responseDTO = PostUpdateResponseDTO.builder()
                .id(postId)
                .title("title")
                .description("description")
                .content("content")
                .slug("slug")
                .previewImageUrl("previewImageUrl")
                .updatedAt(now)
                .build();
        when(postService.updatePostById(anyLong(), any(PostUpdateRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(patch("/posts/{postId}", postId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.slug").value("slug"))
                .andExpect(jsonPath("$.previewImageUrl").value("previewImageUrl"))
                .andExpect(jsonPath("$.updatedAt").value(now.toString()));

        verify(postService, times(1)).updatePostById(eq(postId), any());
    }

    @Test
    void updatePostById_shouldUpdatePost_whenSomeFieldsAreProvided() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Long postId = 1L;
        String requestBody = """
                {
                    "title": "title",
                    "content": "content"
                }
                """;

        PostUpdateResponseDTO responseDTO = PostUpdateResponseDTO.builder()
                .id(postId)
                .title("title")
                .content("content")
                .updatedAt(now)
                .build();
        when(postService.updatePostById(anyLong(), any(PostUpdateRequestDTO.class)))
                .thenReturn(responseDTO);

        try {
            mockMvc.perform(patch("/posts/{postId}", postId)
                            .contentType("application/json")
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(postId))
                    .andExpect(jsonPath("$.title").value("title"))
                    .andExpect(jsonPath("$.content").value("content"))
                    .andExpect(jsonPath("$.updatedAt").value(now.toString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(postService, times(1)).updatePostById(eq(postId), any());
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn404_whenPostDoesNotExist() {
        Long postId = 1L;
        String requestBody = """
                {
                    "title": "title"
                }
                """;

        when(postService.updatePostById(anyLong(), any(PostUpdateRequestDTO.class)))
                .thenThrow(new PostNotFoundException(null));

        mockMvc.perform(patch("/posts/{postId}", postId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found"));

        verify(postService, times(1)).updatePostById(eq(postId), any());
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn500_whenServiceThrowsFailedToUpdatePostException() {
        Long postId = 1L;
        String requestBody = """
                {
                    "title": "title"
                }
                """;

        when(postService.updatePostById(anyLong(), any(PostUpdateRequestDTO.class)))
                .thenThrow(new FailedToUpdatePostException(null));

        mockMvc.perform(patch("/posts/{postId}", postId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to update post"));

        verify(postService, times(1)).updatePostById(eq(postId), any());
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn400_whenRequestBodyIsNull() {
        Long postId = 1L;

        mockMvc.perform(patch("/posts/{postId}", postId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0]").value("Request body is required"));

        verify(postService, never()).updatePostById(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn400_whenNoFieldsAreProvided() {
        Long postId = 1L;
        String requestBody = """
                {
                }
                """;

        mockMvc.perform(patch("/posts/{postId}", postId)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0]").value("At least one field must be provided"));

        verify(postService, never()).updatePostById(anyLong(), any());
    }
}
