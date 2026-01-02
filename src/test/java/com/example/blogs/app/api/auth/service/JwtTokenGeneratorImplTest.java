package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenGeneratorImplTest {

    @Mock
    private JWTService jwtService;

    private JwtTokenGenerator jwtTokenGenerator;

    @BeforeEach
    void setUp() {
        jwtTokenGenerator = new JwtTokenGeneratorImpl(jwtService);
    }

    @Test
    void generateTokens_shouldReturnTokenPairSuccessfully() {
        when(jwtService.generateAccessToken(anyString(), anyMap()))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString(), anyMap()))
                .thenReturn("refreshToken");

        UserEntity user = createUserEntity("https://example.com/profile.jpg");

        TokenPair tokenPair = jwtTokenGenerator.generateTokens(user);

        assertThat(tokenPair.accessToken()).isEqualTo("accessToken");
        assertThat(tokenPair.refreshToken()).isEqualTo("refreshToken");
        verify(jwtService).generateAccessToken(anyString(), anyMap());
        verify(jwtService).generateRefreshToken(anyString(), anyMap());
    }

    @Test
    void generateTokens_shouldReturnTokenPairSuccessfully_whenProfilePictureIsNull() {
        when(jwtService.generateAccessToken(anyString(), anyMap()))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString(), anyMap()))
                .thenReturn("refreshToken");

        UserEntity user = createUserEntity(null);

        TokenPair tokenPair = jwtTokenGenerator.generateTokens(user);

        assertThat(tokenPair.accessToken()).isEqualTo("accessToken");
        assertThat(tokenPair.refreshToken()).isEqualTo("refreshToken");
        verify(jwtService).generateAccessToken(anyString(), anyMap());
        verify(jwtService).generateRefreshToken(anyString(), anyMap());
    }

    @Test
    void createClaims_shouldReturnClaimsMapSuccessfully() {
        UserEntity user = createUserEntity("https://example.com/profile.jpg");

        var claims = jwtTokenGenerator.createClaims(user, "access");

        assertThat(claims).containsEntry("username", "testuser")
                .containsEntry("email", "test@gmail.com")
                .containsEntry("profilePictureUrl", "https://example.com/profile.jpg")
                .containsEntry("type", "access");
    }

    @Test
    void createClaims_shouldReturnClaimsMapSuccessfully_whenProfilePictureIsNull() {
        UserEntity user = createUserEntity(null);

        var claims = jwtTokenGenerator.createClaims(user, "refresh");

        assertThat(claims).containsEntry("username", "testuser")
                .containsEntry("email", "test@gmail.com")
                .containsEntry("profilePictureUrl", "")
                .containsEntry("type", "refresh");
    }

    private UserEntity createUserEntity(String profilePictureUrl) {
        return UserEntity.builder()
                .id(1L)
                .username("testuser")
                .profilePictureUrl(profilePictureUrl)
                .email("test@gmail.com")
                .build();
    }
}
