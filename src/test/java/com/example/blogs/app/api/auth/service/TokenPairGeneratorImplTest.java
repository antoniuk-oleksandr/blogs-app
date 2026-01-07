package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.security.JtiGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenPairGeneratorImplTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private JtiGenerator jtiGenerator;

    private TokenPairGenerator tokenPairGenerator;

    @BeforeEach
    void setUp() {
        tokenPairGenerator = new TokenPairGeneratorImpl(jwtService, jtiGenerator);
    }

    @Test
    void generateTokens_shouldReturnTokenPairSuccessfully() {
        when(jtiGenerator.generateJti()).thenReturn("jti");
        when(jwtService.generateAccessToken(anyString(), anyMap()))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString(), anyMap()))
                .thenReturn("refreshToken");

        UserEntity user = createUserEntity("https://example.com/profile.jpg");

        TokenPair tokenPair = tokenPairGenerator.generateTokens(user);

        assertThat(tokenPair.accessToken()).isEqualTo("accessToken");
        assertThat(tokenPair.refreshToken()).isEqualTo("refreshToken");
        verify(jwtService).generateAccessToken(anyString(), anyMap());
        verify(jwtService).generateRefreshToken(anyString(), anyMap());
        verify(jtiGenerator, times(2)).generateJti();
    }

    @Test
    void generateTokens_shouldReturnTokenPairSuccessfully_whenProfilePictureIsNull() {
        when(jtiGenerator.generateJti()).thenReturn("jti");
        when(jwtService.generateAccessToken(anyString(), anyMap()))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString(), anyMap()))
                .thenReturn("refreshToken");

        UserEntity user = createUserEntity(null);

        TokenPair tokenPair = tokenPairGenerator.generateTokens(user);

        assertThat(tokenPair.accessToken()).isEqualTo("accessToken");
        assertThat(tokenPair.refreshToken()).isEqualTo("refreshToken");
        verify(jwtService).generateAccessToken(anyString(), anyMap());
        verify(jwtService).generateRefreshToken(anyString(), anyMap());
        verify(jtiGenerator, times(2)).generateJti();
    }

    @Test
    void createClaims_shouldReturnClaimsMapSuccessfully() {
        UserEntity user = createUserEntity("https://example.com/profile.jpg");

        var claims = tokenPairGenerator.createClaims(user, "access");

        assertThat(claims).containsEntry("username", "testuser")
                .containsEntry("email", "test@gmail.com")
                .containsEntry("profilePictureUrl", "https://example.com/profile.jpg")
                .containsEntry("type", "access");
    }

    @Test
    void createClaims_shouldReturnClaimsMapSuccessfully_whenProfilePictureIsNull() {
        UserEntity user = createUserEntity(null);

        var claims = tokenPairGenerator.createClaims(user, "refresh");

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
