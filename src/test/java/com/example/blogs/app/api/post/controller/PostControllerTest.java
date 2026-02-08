package com.example.blogs.app.api.post.controller;

import com.example.blogs.app.api.auth.fixture.AuthFixtures;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.exception.*;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.service.PostService;
import com.example.blogs.app.exception.ErrorResponseWriter;
import com.example.blogs.app.exception.ExceptionHttpStatusMapper;
import com.example.blogs.app.exception.GlobalExceptionHandler;
import com.example.blogs.app.security.UserPrincipal;
import com.example.blogs.app.security.UserPrincipalAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private ObjectMapper objectMapper;

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
    void deletePostById_shouldReturn500_whenServiceThrowsFailedToDeletePostException() {
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
    void createPost_shouldCreatePost() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        LocalDateTime now = LocalDateTime.now().withNano(0);
        String nowStr = objectMapper.writeValueAsString(now).replace("\"", "");
        PostCreateResponseDTO responseDTO = PostFixtures.postCreateResponseDTO(1L, now);
        when(postService.createPost(anyLong(), any(PostCreateRequestDTO.class), any(MultipartFile.class)))
                .thenReturn(responseDTO);

        MockMultipartFile previewImage = new MockMultipartFile(
                "previewImage",
                "preview.png",
                "image/png",
                "dummyImageContent".getBytes()
        );

        PostCreateRequestDTO requestDTO = PostFixtures.postCreateRequestDTO();
        String postJson = new ObjectMapper().writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        try {
            mockMvc.perform(
                            multipart(HttpMethod.POST, "/posts")
                                    .file(previewImage)
                                    .file(postPart)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value(responseDTO.title()))
                    .andExpect(jsonPath("$.description").value(responseDTO.description()))
                    .andExpect(jsonPath("$.content").value(responseDTO.content()))
                    .andExpect(jsonPath("$.slug").value(responseDTO.slug()))
                    .andExpect(jsonPath("$.previewImageUrl").value(responseDTO.previewImageUrl()))
                    .andExpect(jsonPath("$.createdAt").value(nowStr));
            verify(postService).createPost(1L, requestDTO, previewImage);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void createPost_shouldReturn500_whenServiceThrowsFailedToCreatePostException() {
        when(postService.createPost(anyLong(), any(PostCreateRequestDTO.class), any(MultipartFile.class)))
                .thenThrow(new FailedToCreatePostException(null));

        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockMultipartFile previewImage = new MockMultipartFile(
                "previewImage",
                "preview.png",
                "image/png",
                "dummyImageContent".getBytes()
        );

        PostCreateRequestDTO requestDTO = PostFixtures.postCreateRequestDTO();
        String postJson = new ObjectMapper().writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        try {
            mockMvc.perform(
                            multipart(HttpMethod.POST, "/posts")
                                    .file(previewImage)
                                    .file(postPart)
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Failed to create post"));
            verify(postService).createPost(1L, requestDTO, previewImage);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldUpdatePost_whenAllFieldsAndFileAreProvided() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String nowStr = objectMapper.writeValueAsString(now).replace("\"", "");
        Long postId = 1L;

        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Content"
        );

        MockMultipartFile previewImage = new MockMultipartFile(
                "previewImage",
                "updated-preview.png",
                "image/png",
                "updatedImageContent".getBytes()
        );

        String postJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        PostUpdateResponseDTO responseDTO = PostUpdateResponseDTO.builder()
                .id(postId)
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .slug("updated-title")
                .previewImageUrl("newPreviewImageUrl")
                .updatedAt(now)
                .build();

        when(postService.updatePostById(eq(postId), any(PostUpdateRequestDTO.class), any(MultipartFile.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/posts/{postId}", postId)
                                .file(previewImage)
                                .file(postPart)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.slug").value("updated-title"))
                .andExpect(jsonPath("$.previewImageUrl").value("newPreviewImageUrl"))
                .andExpect(jsonPath("$.updatedAt").value(nowStr));

        verify(postService).updatePostById(postId, requestDTO, previewImage);
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldUpdatePost_whenNoFileIsProvided() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String nowStr = objectMapper.writeValueAsString(now).replace("\"", "");
        Long postId = 1L;

        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Content"
        );

        String postJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        PostUpdateResponseDTO responseDTO = PostUpdateResponseDTO.builder()
                .id(postId)
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .slug("updated-title")
                .previewImageUrl("existingPreviewImageUrl")
                .updatedAt(now)
                .build();

        when(postService.updatePostById(eq(postId), any(PostUpdateRequestDTO.class), eq(null)))
                .thenReturn(responseDTO);

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/posts/{postId}", postId)
                                .file(postPart)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.slug").value("updated-title"))
                .andExpect(jsonPath("$.previewImageUrl").value("existingPreviewImageUrl"))
                .andExpect(jsonPath("$.updatedAt").value(nowStr));

        verify(postService).updatePostById(postId, requestDTO, null);
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldUpdatePost_whenOnlySomeFieldsAreProvided() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String nowStr = objectMapper.writeValueAsString(now).replace("\"", "");
        Long postId = 1L;

        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                null,
                "Updated Description",
                "Updated Content"
        );

        String postJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        PostUpdateResponseDTO responseDTO = PostUpdateResponseDTO.builder()
                .id(postId)
                .title("Existing Title")
                .description("Updated Description")
                .content("Updated Content")
                .slug("existing-slug")
                .previewImageUrl("existingPreviewImageUrl")
                .updatedAt(now)
                .build();

        when(postService.updatePostById(eq(postId), any(PostUpdateRequestDTO.class), eq(null)))
                .thenReturn(responseDTO);

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/posts/{postId}", postId)
                                .file(postPart)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Existing Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.slug").value("existing-slug"))
                .andExpect(jsonPath("$.previewImageUrl").value("existingPreviewImageUrl"))
                .andExpect(jsonPath("$.updatedAt").value(nowStr));

        verify(postService).updatePostById(postId, requestDTO, null);
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn404_whenPostDoesNotExist() {
        Long postId = 1L;

        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Content"
        );

        String postJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        when(postService.updatePostById(eq(postId), any(PostUpdateRequestDTO.class), eq(null)))
                .thenThrow(new PostNotFoundException(null));

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/posts/{postId}", postId)
                                .file(postPart)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post not found"));

        verify(postService).updatePostById(eq(postId), any(PostUpdateRequestDTO.class), eq(null));
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn500_whenServiceThrowsFailedToUpdatePostException() {
        Long postId = 1L;

        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Content"
        );

        String postJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        when(postService.updatePostById(eq(postId), any(PostUpdateRequestDTO.class), eq(null)))
                .thenThrow(new FailedToUpdatePostException(null));

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/posts/{postId}", postId)
                                .file(postPart)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to update post"));

        verify(postService).updatePostById(eq(postId), any(PostUpdateRequestDTO.class), eq(null));
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn500_whenServiceThrowsUnexpectedException() {
        Long postId = 1L;

        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Content"
        );

        String postJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        when(postService.updatePostById(eq(postId), any(PostUpdateRequestDTO.class), eq(null)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/posts/{postId}", postId)
                                .file(postPart)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"));

        verify(postService).updatePostById(eq(postId), any(PostUpdateRequestDTO.class), eq(null));
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn400_whenRequestBodyIsNull() {
        Long postId = 1L;

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/posts/{postId}", postId)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required part 'post' is not present."));

        verify(postService, never()).updatePostById(anyLong(), any(), any());
    }

    @Test
    @SneakyThrows
    void updatePostById_shouldReturn400_whenNoFieldsAreProvided() {
        Long postId = 1L;

        PostUpdateRequestDTO requestDTO = new PostUpdateRequestDTO(
                null,
                null,
                null
        );

        String postJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile postPart = new MockMultipartFile(
                "post",
                "",
                "application/json",
                postJson.getBytes()
        );

        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/posts/{postId}", postId)
                                .file(postPart)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.errors[0]").value("At least one field must be provided"));

        verify(postService, never()).updatePostById(anyLong(), any(), any());
    }
}
