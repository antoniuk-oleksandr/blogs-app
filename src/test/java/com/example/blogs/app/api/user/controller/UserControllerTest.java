package com.example.blogs.app.api.user.controller;

import com.example.blogs.app.api.auth.fixture.AuthFixtures;
import com.example.blogs.app.api.user.dto.UpdateUserRequestDTO;
import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDTO;
import com.example.blogs.app.api.user.exception.*;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import com.example.blogs.app.api.user.service.UserService;
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

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GlobalExceptionHandler.class,
        ExceptionHttpStatusMapper.class,
        ErrorResponseWriter.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @SneakyThrows
    void getUserByUsername_shouldReturn200_whenUserExists() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String nowStr = objectMapper.writeValueAsString(now).replace("\"", "");
        List<UserPostSummaryDTO> mockPostSummaries = List.of(
                createMockPostSummary(now),
                createMockPostSummary(now)
        );
        UserDTO userDTO = UserDTO.builder()
                .username("username")
                .bio("bio")
                .profilePictureUrl("profilePictureUrl")
                .posts(mockPostSummaries)
                .build();
        when(userService.getUserByUsername(anyString())).thenReturn(userDTO);

        mockMvc.perform(get("/users/{username}", "username"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.bio").value("bio"))
                .andExpect(jsonPath("$.profilePictureUrl").value("profilePictureUrl"))
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.posts[0].title").value("title"))
                .andExpect(jsonPath("$.posts[0].description").value("description"))
                .andExpect(jsonPath("$.posts[0].slug").value("slug"))
                .andExpect(jsonPath("$.posts[0].previewImageUrl").value("previewImageUrl"))
                .andExpect(jsonPath("$.posts[1].createdAt").value(nowStr));

        verify(userService).getUserByUsername("username");
    }

    @Test
    @SneakyThrows
    void getUserByUsername_shouldReturn404_whenUserDoesNotExist() {
        when(userService.getUserByUsername(anyString())).thenThrow(new UserNotFoundException(null));

        mockMvc.perform(get("/users/{username}", "nonexistentuser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(userService).getUserByUsername("nonexistentuser");
    }

    @Test
    @SneakyThrows
    void getUserByUsername_shouldReturn500_whenFailedToFindUserExceptionOccurs() {
        when(userService.getUserByUsername(anyString())).thenThrow(new FailedToFindUserException(null));

        mockMvc.perform(get("/users/{username}", "someuser"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to find user"));

        verify(userService).getUserByUsername("someuser");
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldUpdateProfile_whenAllFieldsAndFileAreProvided() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserRequestDTO requestDTO = UserFixtures.updateUserRequestDTO();
        String userJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                userJson.getBytes()
        );

        MockMultipartFile profilePicture = new MockMultipartFile(
                "profilePicture",
                "profile.png",
                "image/png",
                "dummyImageContent".getBytes()
        );

        UpdateUserResponseDTO responseDTO = UserFixtures.updateUserResponseDTO(
                "newusername", "Updated bio", "newProfilePictureUrl"
        );

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), any(MultipartFile.class)))
                .thenReturn(responseDTO);

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                                    .file(userPart)
                                    .file(profilePicture)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("newusername"))
                    .andExpect(jsonPath("$.bio").value("Updated bio"))
                    .andExpect(jsonPath("$.profilePictureUrl").value("newProfilePictureUrl"));

            verify(userService).updateUserProfile(1L, requestDTO, profilePicture);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldUpdateProfile_whenNoFileIsProvided() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserRequestDTO requestDTO = UserFixtures.updateUserRequestDTO();
        String userJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                userJson.getBytes()
        );

        UpdateUserResponseDTO responseDTO = UserFixtures.updateUserResponseDTO(
                "newusername", "Updated bio", "existingProfilePictureUrl"
        );

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null)))
                .thenReturn(responseDTO);

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                                    .file(userPart)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("newusername"))
                    .andExpect(jsonPath("$.bio").value("Updated bio"))
                    .andExpect(jsonPath("$.profilePictureUrl").value("existingProfilePictureUrl"));

            verify(userService).updateUserProfile(1L, requestDTO, null);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldUpdateProfile_whenOnlySomeFieldsAreProvided() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO(
                null,
                "Updated bio only",
                null,
                null
        );
        String userJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                userJson.getBytes()
        );

        UpdateUserResponseDTO responseDTO = UserFixtures.updateUserResponseDTO(
                "existingusername", "Updated bio only", "existingProfilePictureUrl"
        );

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null)))
                .thenReturn(responseDTO);

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                                    .file(userPart)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("existingusername"))
                    .andExpect(jsonPath("$.bio").value("Updated bio only"))
                    .andExpect(jsonPath("$.profilePictureUrl").value("existingProfilePictureUrl"));

            verify(userService).updateUserProfile(1L, requestDTO, null);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldReturn404_whenUserNotFound() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserRequestDTO requestDTO = UserFixtures.updateUserRequestDTO();
        String userJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                userJson.getBytes()
        );

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null)))
                .thenThrow(new UserNotFoundException(null));

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                                    .file(userPart)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));

            verify(userService).updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldReturn409_whenUsernameIsTaken() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserRequestDTO requestDTO = UserFixtures.updateUserRequestDTO();
        String userJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                userJson.getBytes()
        );

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null)))
                .thenThrow(new UsernameTakenException(null));

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                                    .file(userPart)
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Username is already taken"));

            verify(userService).updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldReturn409_whenEmailIsTaken() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserRequestDTO requestDTO = UserFixtures.updateUserRequestDTO();
        String userJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                userJson.getBytes()
        );

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null)))
                .thenThrow(new EmailTakenException(null));

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                                    .file(userPart)
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Email is already taken"));

            verify(userService).updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldReturn500_whenServiceThrowsFailedToUpdateUserException() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserRequestDTO requestDTO = UserFixtures.updateUserRequestDTO();
        String userJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                userJson.getBytes()
        );

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null)))
                .thenThrow(new FailedToUpdateUserException(null));

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                                    .file(userPart)
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Failed to update user"));

            verify(userService).updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldReturn500_whenServiceThrowsUnexpectedException() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateUserRequestDTO requestDTO = UserFixtures.updateUserRequestDTO();
        String userJson = objectMapper.writeValueAsString(requestDTO);
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                userJson.getBytes()
        );

        when(userService.updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null)))
                .thenThrow(new RuntimeException("Unexpected error"));

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                                    .file(userPart)
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value("Unexpected error"));

            verify(userService).updateUserProfile(eq(1L), any(UpdateUserRequestDTO.class), eq(null));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void updateUserProfile_shouldReturn400_whenRequestBodyIsNull() {
        Jwt jwt = AuthFixtures.jwt();
        UserPrincipal userPrincipal = AuthFixtures.userPrincipal();
        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            mockMvc.perform(
                            multipart(HttpMethod.PATCH, "/users/me")
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Required part 'user' is not present."));

            verify(userService, never()).updateUserProfile(anyLong(), any(), any());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private UserPostSummaryDTO createMockPostSummary(LocalDateTime time) {
        return UserPostSummaryDTO.builder()
                .title("title")
                .description("description")
                .slug("slug")
                .previewImageUrl("previewImageUrl")
                .createdAt(time)
                .build();
    }
}
