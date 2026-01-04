package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.exception.FailedToParseClaimsException;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTHelperImplTest {

    @Mock
    private JwtParser jwtParser;

    @Mock
    private Jws<Claims> jws;

    @Mock
    private Jwt jwt;

    private JWTHelperImpl jwtHelper;

    @BeforeEach
    void setUp() {
        String secretKey = "my-very-secure-secret-key-at-least-256-bits-long-for-hs256";
        jwtHelper = new JWTHelperImpl(secretKey, jwtParser);
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

    @Test
    void parseClaims_shouldReturnClaimsSuccessfully() {
        Claims claims = Jwts.claims().build();

        when(jws.getPayload()).thenReturn(claims);
        when(jwtParser.parseSignedClaims(anyString())).thenReturn(jws);

        Map<String, Object> result = jwtHelper.parseClaims("token");

        assertThat(result).isNotNull();
        verify(jwtParser).parseSignedClaims("token");
    }

    @Test
    void parseClaims_shouldReturnClaimsWithData() {
        Claims claims = Jwts.claims()
                .subject("1")
                .add("username", "test")
                .build();

        when(jws.getPayload()).thenReturn(claims);
        when(jwtParser.parseSignedClaims(anyString())).thenReturn(jws);

        Map<String, Object> result = jwtHelper.parseClaims("token");

        assertThat(result)
                .isNotNull()
                .containsEntry("sub", "1")
                .containsEntry("username", "test");
        verify(jwtParser).parseSignedClaims("token");
    }

    @Test
    void parseClaims_shouldThrowException_whenTokenIsInvalid() {
        when(jwtParser.parseSignedClaims(anyString()))
                .thenThrow(new RuntimeException("Invalid token"));

        assertThatThrownBy(() -> jwtHelper.parseClaims("invalid-token"))
                .isInstanceOf(FailedToParseClaimsException.class);
        verify(jwtParser).parseSignedClaims("invalid-token");
    }

    @Test
    void validateToken_shouldReturnTrue_whenTokenIsValid() {
        when(jwtParser.parse(anyString()))
                .thenReturn(jwt);

        boolean isValid = jwtHelper.validateToken("valid-token");

        assertThat(isValid).isTrue();
        verify(jwtParser).parse("valid-token");
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
        when(jwtParser.parse(anyString()))
                .thenThrow(new RuntimeException("Invalid token"));

        boolean isValid = jwtHelper.validateToken("invalid-token");

        assertThat(isValid).isFalse();
        verify(jwtParser).parse("invalid-token");
    }
}
