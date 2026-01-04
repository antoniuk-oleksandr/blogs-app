package com.example.blogs.app.config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for JWT parsing and validation using JJWT library.
 * Provides a shared JwtParser bean for token verification across the application.
 */
@Configuration
public class JWTConfig {

    private final SecretKey signingKey;

    /**
     * Constructs JWTConfig with HMAC SHA256 signing key from application properties.
     *
     * @param secretKey the secret key for JWT signing and verification
     */
    public JWTConfig(@Value("${jwt.secret-key}") String secretKey) {
        this.signingKey = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }

    /**
     * Creates a JWT parser bean configured with the signing key for token verification.
     *
     * @return configured JwtParser for parsing and validating JWT tokens
     */
    @Bean
    public JwtParser jwtParser() {
        return Jwts.parser().verifyWith(signingKey).build();
    }
}
