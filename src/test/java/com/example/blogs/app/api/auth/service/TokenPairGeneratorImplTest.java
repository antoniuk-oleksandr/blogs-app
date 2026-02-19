package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import com.example.blogs.app.security.JtiGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

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
        Long fileId = 1L;
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);

        when(jtiGenerator.generateJti()).thenReturn("jti");
        when(jwtService.generateAccessToken(anyString(), anyMap()))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(anyString(), anyMap()))
                .thenReturn("refreshToken");

        TokenPair tokenPair = tokenPairGenerator.generateTokens(user);

        assertThat(tokenPair.accessToken()).isEqualTo("accessToken");
        assertThat(tokenPair.refreshToken()).isEqualTo("refreshToken");
        verify(jwtService).generateAccessToken(anyString(), anyMap());
        verify(jwtService).generateRefreshToken(anyString(), anyMap());
        verify(jtiGenerator, times(2)).generateJti();
    }

    @Test
    void createClaims_shouldReturnClaimsMapSuccessfully() {
        Long fileId = 1L;
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);

        Map<String, Object> claims = tokenPairGenerator.createClaims(user, "access");

        assertThat(claims)
                .containsEntry("id", user.getId().toString())
                .containsEntry("username", user.getUsername())
                .containsEntry("email", user.getEmail())
                .containsEntry("type", "access");
    }
}
