package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.RegisterRequest;
import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userService, passwordEncoder, jwtService);
    }

    @Test
    void register_shouldHashPasswordAndCreateUser() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", "email@gmail.com");
        String hashedPassword = "hashedPassword";
        UserEntity createdUser = createUser(1L, "testuser", "email@gmail.com", hashedPassword);

        stubPasswordEncoding("password123", hashedPassword);
        stubUserCreation(createdUser);
        stubTokenGeneration("testuser", "accessToken", "refreshToken");

        TokenPair result = authService.register(request);

        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");

        verify(passwordEncoder).encode("password123");
        verifyUserCreatedWith("testuser", "email@gmail.com", hashedPassword);
        verifyTokensGeneratedFor("testuser");
    }

    @Test
    void register_shouldUseUsernameAsTokenSubject() {
        RegisterRequest request = new RegisterRequest("john_doe", "securepass", "john@example.com");
        UserEntity createdUser = createUser(2L, "john_doe", "john@example.com", "hashed");

        stubPasswordEncoding(anyString(), "hashed");
        stubUserCreation(createdUser);
        stubTokenGeneration("john_doe", "access", "refresh");

        authService.register(request);

        verifyTokensGeneratedFor("john_doe");
    }

    @Test
    void register_shouldNotStoreRawPassword() {
        RegisterRequest request = new RegisterRequest("testuser", "plainPassword", "test@example.com");

        stubPasswordEncoding("plainPassword", "hashed");
        stubUserCreation(createUser(1L, "testuser", "test@example.com", "hashed"));
        stubTokenGeneration("testuser", "token", "token");

        authService.register(request);

        verifyPasswordWasHashed("plainPassword", "hashed");
    }

    private UserEntity createUser(Long id, String username, String email, String passwordHash) {
        return UserEntity.builder()
                .id(id)
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .build();
    }

    private void stubPasswordEncoding(String rawPassword, String hashedPassword) {
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
    }

    private void stubUserCreation(UserEntity user) {
        when(userService.createUser(any(CreateUserCommand.class))).thenReturn(user);
    }

    private void stubTokenGeneration(String username, String accessToken, String refreshToken) {
        when(jwtService.generateAccessToken(username)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(username)).thenReturn(refreshToken);
    }

    private void verifyUserCreatedWith(String username, String email, String passwordHash) {
        ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
        verify(userService).createUser(captor.capture());

        CreateUserCommand captured = captor.getValue();
        assertThat(captured.username()).isEqualTo(username);
        assertThat(captured.email()).isEqualTo(email);
        assertThat(captured.passwordHash()).isEqualTo(passwordHash);
    }

    private void verifyTokensGeneratedFor(String username) {
        verify(jwtService).generateAccessToken(username);
        verify(jwtService).generateRefreshToken(username);
    }

    private void verifyPasswordWasHashed(String rawPassword, String hashedPassword) {
        ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
        verify(userService).createUser(captor.capture());

        assertThat(captor.getValue().passwordHash()).isNotEqualTo(rawPassword);
        assertThat(captor.getValue().passwordHash()).isEqualTo(hashedPassword);
    }
}