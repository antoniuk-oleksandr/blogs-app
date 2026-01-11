package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.*;
import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import com.example.blogs.app.api.auth.exception.FailedToParseClaimsException;
import com.example.blogs.app.api.auth.exception.InvalidCredentialsException;
import com.example.blogs.app.api.auth.exception.UnauthorizedException;
import com.example.blogs.app.api.auth.repository.adapter.RevokedTokenRepositoryAdapter;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.service.UserService;
import com.example.blogs.app.security.Hasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenPairGenerator tokenPairGenerator;

    @Mock
    private JWTService jwtService;

    @Mock
    RevokedTokenRepositoryAdapter revokedTokenRepositoryAdapter;

    @Mock
    private Hasher hasher;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userService,
                passwordEncoder,
                tokenPairGenerator,
                jwtService,
                revokedTokenRepositoryAdapter,
                hasher
        );
    }

    @Test
    void register_shouldHashPasswordAndCreateUser() {
        RegisterRequest request = new RegisterRequest("testuser", "password123", "email@gmail.com");
        String hashedPassword = "hashedPassword";
        UserEntity createdUser = createUser(1L, "testuser", "email@gmail.com", hashedPassword);

        stubPasswordEncoding("password123", hashedPassword);
        stubUserCreation(createdUser);
        stubTokenGeneration("access", "refresh");

        TokenPair result = authService.register(request);

        assertThat(result.accessToken()).isEqualTo("access");
        assertThat(result.refreshToken()).isEqualTo("refresh");

        verify(passwordEncoder).encode("password123");
        verifyUserCreatedWith("testuser", "email@gmail.com", hashedPassword);
    }

    @Test
    void register_shouldUseUsernameAsTokenSubject() {
        RegisterRequest request = new RegisterRequest("john_doe", "securepass", "john@example.com");
        UserEntity createdUser = createUser(2L, "john_doe", "john@example.com", "hashed");

        stubPasswordEncoding(anyString(), "hashed");
        stubUserCreation(createdUser);
        stubTokenGeneration("access", "refresh");

        TokenPair tokenPair = authService.register(request);
        assertThat(tokenPair.accessToken()).isEqualTo("access");
        assertThat(tokenPair.refreshToken()).isEqualTo("refresh");
    }

    @Test
    void register_shouldNotStoreRawPassword() {
        RegisterRequest request = new RegisterRequest("testuser", "plainPassword", "test@example.com");

        stubPasswordEncoding("plainPassword", "hashed");
        stubUserCreation(createUser(1L, "testuser", "test@example.com", "hashed"));
        stubTokenGeneration("access", "refresh");

        TokenPair tokenPair = authService.register(request);

        verifyPasswordWasHashed("plainPassword", "hashed");
        assertThat(tokenPair.accessToken()).isEqualTo("access");
        assertThat(tokenPair.refreshToken()).isEqualTo("refresh");
    }

    @Test
    void login_shouldReturnLoginRequestSuccessfully() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        UserEntity mockUser = createUser(1L, "testuser", "email@gmail.com", "hashedPassword");

        when(userService.findUserByUsernameOrEmail("testuser")).thenReturn(mockUser);
        when(passwordEncoder.matches(any(CharSequence.class), anyString())).thenReturn(true);
        stubTokenGeneration("access", "refresh");

        TokenPair tokenPair = authService.login(loginRequest);

        assertThat(tokenPair.accessToken()).isEqualTo("access");
        assertThat(tokenPair.refreshToken()).isEqualTo("refresh");
        verify(userService).findUserByUsernameOrEmail("testuser");
        verify(passwordEncoder).matches("password123", "hashedPassword");
    }

    @Test
    void login_shouldThrowUnauthorizedExceptionForInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest("invalidUser", "wrongPassword");

        when(userService.findUserByUsernameOrEmail("invalidUser")).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(Exception.class);

        verify(userService).findUserByUsernameOrEmail("invalidUser");
        verify(passwordEncoder, never()).matches(any(CharSequence.class), anyString());
        verify(tokenPairGenerator, never()).generateTokens(any(UserEntity.class));
    }

    @Test
    void login_shouldThrowUnauthorizedException_whenPasswordIsWrong() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        UserEntity mockUser = createUser(1L, "testuser", "email@gmail.com", "hashedPassword");

        when(userService.findUserByUsernameOrEmail("testuser")).thenReturn(mockUser);
        when(passwordEncoder.matches(any(CharSequence.class), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userService).findUserByUsernameOrEmail("testuser");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(tokenPairGenerator, never()).generateTokens(any(UserEntity.class));
    }

    @Test
    void refreshAccessToken_shouldReturnNewAccessToken() {
        Map<String, Object> accessTokenClaims = Map.ofEntries(
                Map.entry("username", "testuser"),
                Map.entry("email", "test@gmail.com"),
                Map.entry("profilePictureUrl", "test")
        );

        Map<String, Object> fullClaims = new HashMap<>(accessTokenClaims);
        fullClaims.put("sub", "1");
        fullClaims.put("type", "refresh");

        when(hasher.hash(anyString())).thenReturn("hashedRefreshToken");
        when(revokedTokenRepositoryAdapter.isTokenRevoked(anyString())).thenReturn(false);
        when(jwtService.parseClaims(anyString())).thenReturn(fullClaims);
        when(jwtService.generateAccessToken(anyString(), anyMap()))
                .thenReturn("newAccessToken");

        AccessTokenResponse accessTokenResponse = authService
                .refreshAccessToken(new RefreshTokenRequest("validRefreshToken"));

        assertThat(accessTokenResponse.accessToken()).isEqualTo("newAccessToken");
        verify(jwtService).generateAccessToken("1", accessTokenClaims);
    }

    @Test
    void refreshAccessToken_shouldThrowUnauthorizedException_whenTokenIsInvalid() {
        when(hasher.hash(anyString())).thenReturn("hashedRefreshToken");
        when(revokedTokenRepositoryAdapter.isTokenRevoked(anyString())).thenReturn(false);
        when(jwtService.parseClaims(anyString())).thenThrow(FailedToParseClaimsException.class);

        RefreshTokenRequest request = new RefreshTokenRequest("invalidRefreshToken");

        assertThatThrownBy(() -> authService.refreshAccessToken(request))
                .isInstanceOf(UnauthorizedException.class);

        verify(jwtService).parseClaims(anyString());
        verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
    }

    @Test
    void refreshAccessToken_shouldThrowUnauthorizedException_whenTokenIsNotRefreshType() {
        Map<String, Object> claims = Map.of(
                "sub", "1",
                "type", "access"
        );

        when(hasher.hash(anyString())).thenReturn("hashedRefreshToken");
        when(revokedTokenRepositoryAdapter.isTokenRevoked(anyString())).thenReturn(false);
        when(jwtService.parseClaims(anyString())).thenReturn(claims);

        RefreshTokenRequest request = new RefreshTokenRequest("invalidTypeToken");

        assertThatThrownBy(() -> authService.refreshAccessToken(request))
                .isInstanceOf(UnauthorizedException.class);

        verify(jwtService).parseClaims("invalidTypeToken");
        verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
    }

    @Test
    void refreshAccessToken_shouldThrowUnauthorizedException_whenTokenIsRevoked() {
        when(hasher.hash(anyString())).thenReturn("hashedRefreshToken");
        when(revokedTokenRepositoryAdapter.isTokenRevoked("hashedRefreshToken")).thenReturn(true);

        RefreshTokenRequest request = new RefreshTokenRequest("revokedToken");

        assertThatThrownBy(() -> authService.refreshAccessToken(request))
                .isInstanceOf(UnauthorizedException.class);

        verify(revokedTokenRepositoryAdapter).isTokenRevoked(anyString());
        verify(jwtService, never()).parseClaims(anyString());
        verify(jwtService, never()).generateAccessToken(anyString(), anyMap());
    }

    @Test
    void logout_shouldRevokeRefreshTokenSuccessfully() {
        LocalDateTime now = LocalDateTime.now().plusHours(1).withNano(0);
        Map<String, Object> claims = Map.ofEntries(
                Map.entry("exp", now.toEpochSecond(ZoneOffset.UTC))
        );
        RevokedTokenEntity mockedEntity = RevokedTokenEntity.builder()
                .id(1L)
                .token("hashedToken")
                .expiresAt(now)
                .build();

        when(jwtService.parseClaims(anyString())).thenReturn(claims);
        when(hasher.hash(anyString())).thenReturn("hashedToken");
        when(revokedTokenRepositoryAdapter.saveRevokedToken(anyString(), any(LocalDateTime.class)))
                .thenReturn(mockedEntity);

        LogoutRequest request = new LogoutRequest("validRefreshToken");
        RevokedTokenEntity revokedToken = authService.logout(request);

        assertThat(revokedToken).isNotNull();
        verify(jwtService).parseClaims("validRefreshToken");
        verify(hasher).hash("validRefreshToken");
        verify(revokedTokenRepositoryAdapter).saveRevokedToken("hashedToken", now);
    }

    @Test
    void logout_shouldThrowUnauthorizedException_whenTokenIsInvalid() {
        when(jwtService.parseClaims(anyString())).thenThrow(FailedToParseClaimsException.class);

        LogoutRequest request = new LogoutRequest("invalidRefreshToken");

        assertThatThrownBy(() -> authService.logout(request))
                .isInstanceOf(UnauthorizedException.class);

        verify(jwtService).parseClaims("invalidRefreshToken");
        verify(hasher, never()).hash(anyString());
        verify(revokedTokenRepositoryAdapter, never()).saveRevokedToken(anyString(), any(LocalDateTime.class));
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

    private void stubTokenGeneration(String accessToken, String refreshToken) {
        when(tokenPairGenerator.generateTokens(any(UserEntity.class)))
                .thenReturn(new TokenPair(accessToken, refreshToken));
    }

    private void verifyUserCreatedWith(String username, String email, String passwordHash) {
        ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
        verify(userService).createUser(captor.capture());

        CreateUserCommand captured = captor.getValue();
        assertThat(captured.username()).isEqualTo(username);
        assertThat(captured.email()).isEqualTo(email);
        assertThat(captured.passwordHash()).isEqualTo(passwordHash);
    }


    private void verifyPasswordWasHashed(String rawPassword, String hashedPassword) {
        ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
        verify(userService).createUser(captor.capture());

        assertThat(captor.getValue().passwordHash()).isNotEqualTo(rawPassword);
        assertThat(captor.getValue().passwordHash()).isEqualTo(hashedPassword);
    }
}