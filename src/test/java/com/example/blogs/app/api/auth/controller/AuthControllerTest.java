package com.example.blogs.app.api.auth.controller;

import com.example.blogs.app.api.auth.dto.*;
import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import com.example.blogs.app.api.auth.exception.UnauthorizedException;
import com.example.blogs.app.api.auth.service.AuthService;
import com.example.blogs.app.exception.ExceptionHttpStatusMapper;
import com.example.blogs.app.exception.GlobalExceptionHandler;
import com.example.blogs.app.security.UserPrincipal;
import com.example.blogs.app.security.UserPrincipalAuthenticationToken;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, ExceptionHttpStatusMapper.class})
@AutoConfigureMockMvc(addFilters = false)


//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @SneakyThrows
    void register_shouldReturn201_whenSuccessfulRegistration() {
        when(authService.register(any(RegisterRequest.class))).thenReturn(
                new TokenPair("accessToken", "refreshToken")
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "username": "test",
                                "password": "test12345",
                                "email": "test@gmail.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @SneakyThrows
    void register_shouldReturnRequiredEmail_whenRequestBodyEmailIsNotStated() {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "test",
                                  "password": "test12345"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Email is required"));

    }

    @Test
    @SneakyThrows
    void register_shouldReturnRequiredEmail_whenRequestBodyPasswordIsNotStated() {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "test",
                                  "email": "test@gmail.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*]").value("Password is required"));
    }

    @Test
    @SneakyThrows
    void register_shouldReturnRequiredEmail_whenRequestBodyUsernameIsNotStated() {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "test12345",
                                  "email": "test@gmail.com"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Username is required"));
    }

    @Test
    @SneakyThrows
    void register_shouldReturnRequiredEmail_whenRequestBodyIsNotStated() {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Request body is required"));
    }

    @Test
    @SneakyThrows
    void login_shouldReturn200_whenSuccessfulLogin() {
        when(authService.login(any(LoginRequest.class))).thenReturn(
                new TokenPair("accessToken", "refreshToken")
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "usernameOrEmail": "test",
                                    "password": "test12345"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    @SneakyThrows
    void login_shouldReturnRequiredEmail_whenRequestBodyIsNotStated() {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Request body is required"));
    }

    @Test
    @SneakyThrows
    void login_shouldReturnRequiredEmail_whenRequestBodyPasswordIsNotStated() {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usernameOrEmail": "test"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Password is required"));
    }

    @Test
    @SneakyThrows
    void login_shouldReturnRequiredEmail_whenRequestBodyUsernameOrEmailIsNotStated() {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "test12345"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Username or email is required"));
    }

    @Test
    @SneakyThrows
    void me_shouldReturn200_whenSuccessful() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .claim("sub", "1")
                .claim("username", "test")
                .claim("email", "test@gmail.com")
                .claim("profilePictureUrl", "picture")
                .issuedAt(java.time.Instant.now())
                .expiresAt(java.time.Instant.now().plusSeconds(3600))
                .build();


        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "test",
                "test@gmail.com",
                "picture"
        );

        Authentication auth = new UserPrincipalAuthenticationToken(userPrincipal, jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            mockMvc.perform(get("/auth/me")
                            .accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("test"))
                    .andExpect(jsonPath("$.email").value("test@gmail.com"))
                    .andExpect(jsonPath("$.profilePictureUrl").value("picture"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @SneakyThrows
    void refreshToken_shouldReturn200_whenSuccessful() {
        when(authService.refreshAccessToken(any())).thenReturn(
                new AccessTokenResponse("newAccessToken")
        );

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "someRefreshToken"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("newAccessToken"));
    }

    @Test
    @SneakyThrows
    void refreshToken_shouldReturn400_whenRefreshTokenIsNotStated() {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("Refresh token is required"));
    }

    @Test
    @SneakyThrows
    void refreshToken_shouldReturn401_whenRefreshTokenIsInvalid() {
        when(authService.refreshAccessToken(any()))
                .thenThrow(new UnauthorizedException());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "invalidRefreshToken"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Unauthorized access"));
    }

    @Test
    @SneakyThrows
    void logout_shouldReturn204_whenSuccessful() {
        when(authService.logout(any(LogoutRequest.class))).thenReturn(any(RevokedTokenEntity.class));

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "invalidRefreshToken"
                                }
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void logout_shouldReturn400_whenRefreshTokenIsNotStated() {
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("Refresh token is required"));
    }

    @Test
    @SneakyThrows
    void logout_shouldReturn401_whenRefreshTokenIsInvalid() {
        when(authService.logout(any()))
                .thenThrow(new UnauthorizedException());

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "invalidRefreshToken"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Unauthorized access"));
    }
}
