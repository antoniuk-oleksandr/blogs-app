package com.example.blogs.app.api.auth.fixture;

import com.example.blogs.app.security.UserPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

/**
 * Test fixture factory for creating authentication-related test objects with predefined values.
 */
public class AuthFixtures {

    /**
     * Creates a mock JWT token with predefined claims for testing.
     *
     * @return configured JWT token with test user claims
     */
    public static Jwt jwt() {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .claim("sub", "1")
                .claim("username", "test")
                .claim("email", "test@gmail.com")
                .claim("profilePictureUrl", "picture")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    /**
     * Creates a user principal with predefined test values.
     *
     * @return configured user principal for testing
     */
    public static UserPrincipal userPrincipal() {
        return new UserPrincipal(
                1L,
                "test",
                "test@gmail.com",
                "picture"
        );
    }
}
