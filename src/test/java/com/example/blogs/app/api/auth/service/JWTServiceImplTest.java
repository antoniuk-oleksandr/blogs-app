package com.example.blogs.app.api.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JWTServiceImplTest {

    @Mock
    private JWTHelper jwtHelper;

    private JWTServiceImpl jwtService;

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
}
