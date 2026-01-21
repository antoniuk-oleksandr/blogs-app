package com.example.blogs.app.api.auth.fixture;

import com.example.blogs.app.security.UserPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

public class AuthFixtures {

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

    public static UserPrincipal userPrincipal() {
        return new UserPrincipal(
                1L,
                "test",
                "test@gmail.com",
                "picture"
        );
    }
}
