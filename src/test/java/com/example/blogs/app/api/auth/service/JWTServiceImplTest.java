package com.example.blogs.app.api.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceImplTest {

    @Mock
    private JWTHelper jwtHelper;

    private JWTService jwtService;

    private Duration accessTokenExpiration;
    private Duration refreshTokenExpiration;

    @BeforeEach
    void setUp() {
        accessTokenExpiration = Duration.ofMinutes(15);
        refreshTokenExpiration = Duration.ofDays(7);

        jwtService = new JWTServiceImpl(jwtHelper, accessTokenExpiration, refreshTokenExpiration);
    }

    @Test
    void generateAccessToken_shouldReturnToken() {
        String subject = "user123";
        String fakeToken = "fake-access-token";

        when(jwtHelper.generateToken(subject, accessTokenExpiration))
                .thenReturn(fakeToken);

        String token = jwtService.generateAccessToken(subject);

        assertThat(token).isEqualTo(fakeToken);
        verify(jwtHelper).generateToken(subject, accessTokenExpiration);
    }

    @Test
    void generateRefreshToken_shouldReturnToken() {
        String subject = "user123";
        String fakeToken = "fake-access-token";

        when(jwtHelper.generateToken(subject, refreshTokenExpiration))
                .thenReturn(fakeToken);

        String token = jwtService.generateRefreshToken(subject);

        assertThat(token).isEqualTo(fakeToken);
        verify(jwtHelper).generateToken(subject, refreshTokenExpiration);
    }

    @Test
    void generateAccessToken_shouldReturnToken_whenSubjectAndClaimsProvided() {
        String subject = "user123";
        Map<String, Object> claims = Map.of("role", "USER", "email", "user@example.com");

        stubTokenGeneration(subject, claims, accessTokenExpiration, "access.token.here");

        String result = jwtService.generateAccessToken(subject, claims);

        assertThat(result).isEqualTo("access.token.here");
        verifyTokenGenerated(subject, claims, accessTokenExpiration);
    }

    @Test
    void generateAccessToken_shouldUseCorrectExpiration_whenGeneratingToken() {
        String subject = "user456";
        Map<String, Object> claims = Map.of("username", "testuser");

        stubTokenGeneration(subject, claims, accessTokenExpiration, "token");

        jwtService.generateAccessToken(subject, claims);

        verifyTokenGenerated(subject, claims, accessTokenExpiration);
    }

    @Test
    void generateAccessToken_shouldReturnToken_whenClaimsAreEmpty() {
        String subject = "user789";
        Map<String, Object> emptyClaims = Map.of();

        stubTokenGeneration(subject, emptyClaims, accessTokenExpiration, "access.token.empty");

        String result = jwtService.generateAccessToken(subject, emptyClaims);

        assertThat(result).isEqualTo("access.token.empty");
        verifyTokenGenerated(subject, emptyClaims, accessTokenExpiration);
    }

    @Test
    void generateRefreshToken_shouldReturnToken_whenSubjectAndClaimsProvided() {
        String subject = "user123";
        Map<String, Object> claims = Map.of("role", "USER");

        stubTokenGeneration(subject, claims, refreshTokenExpiration, "refresh.token.here");

        String result = jwtService.generateRefreshToken(subject, claims);

        assertThat(result).isEqualTo("refresh.token.here");
        verifyTokenGenerated(subject, claims, refreshTokenExpiration);
    }

    @Test
    void generateRefreshToken_shouldUseCorrectExpiration_whenGeneratingToken() {
        String subject = "user456";
        Map<String, Object> claims = Map.of("username", "testuser");

        stubTokenGeneration(subject, claims, refreshTokenExpiration, "token");

        jwtService.generateRefreshToken(subject, claims);

        verifyTokenGenerated(subject, claims, refreshTokenExpiration);
    }

    @Test
    void generateRefreshToken_shouldReturnToken_whenClaimsAreEmpty() {
        String subject = "user789";
        Map<String, Object> emptyClaims = Map.of();

        stubTokenGeneration(subject, emptyClaims, refreshTokenExpiration, "refresh.token.empty");

        String result = jwtService.generateRefreshToken(subject, emptyClaims);

        assertThat(result).isEqualTo("refresh.token.empty");
        verifyTokenGenerated(subject, emptyClaims, refreshTokenExpiration);
    }

    @Test
    void validateToken_shouldReturnTrue_whenTokenIsValid() {
        when(jwtHelper.validateToken("token")).thenReturn(true);
        boolean isValid = jwtService.validateToken("token");

        assertThat(isValid).isTrue();
        verify(jwtHelper).validateToken("token");
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
        when(jwtHelper.validateToken("invalid-token")).thenReturn(false);
        boolean isValid = jwtService.validateToken("invalid-token");

        assertThat(isValid).isFalse();
        verify(jwtHelper).validateToken("invalid-token");
    }

    @Test
    void parseClaims_shouldReturnClaims() {
        when(jwtHelper.parseClaims("token"))
                .thenReturn(Map.of("sub", "1", "username", "testuser"));

        Map<String, Object> claims = jwtService.parseClaims("token");
        assertThat(claims).containsEntry("sub", "1")
                          .containsEntry("username", "testuser");
        verify(jwtHelper).parseClaims("token");
    }

    private void stubTokenGeneration(String subject, Map<String, Object> claims, Duration expiration, String token) {
        when(jwtHelper.generateToken(subject, claims, expiration)).thenReturn(token);
    }

    private void verifyTokenGenerated(String subject, Map<String, Object> claims, Duration expiration) {
        verify(jwtHelper).generateToken(subject, claims, expiration);
    }
}
