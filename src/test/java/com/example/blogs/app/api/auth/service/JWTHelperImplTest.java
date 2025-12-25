package com.example.blogs.app.api.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JWTHelperImplTest {
    private JWTHelperImpl jwtHelper;

    @BeforeEach
    void setUp() {
        String secretKey = "my-very-secure-secret-key-at-least-256-bits-long-for-hs256";
        jwtHelper = new JWTHelperImpl(secretKey);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String subject = "user1";
        Duration duration = Duration.ofMinutes(15);

        String token = jwtHelper.generateToken(subject, duration);

        assertThat(token)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void generateToken_withClaims_shouldIncludeCustomClaims() {
        String subject = "user1";
        Duration duration = Duration.ofMinutes(15);
        Map<String, Object> customClaims = Map.of(
                "role", "admin"
        );

        String token = jwtHelper.generateToken(subject, customClaims, duration);

        Claims claims = Jwts.parser()
                .verifyWith(jwtHelper.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(subject);
        assertThat(claims).containsEntry("role", "admin");
    }
}
