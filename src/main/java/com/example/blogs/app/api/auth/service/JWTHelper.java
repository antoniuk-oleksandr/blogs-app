package com.example.blogs.app.api.auth.service;

import java.time.Duration;
import java.util.Map;

/**
 * Low-level utility for generating signed JWT tokens with custom claims and expiration.
 */
public interface JWTHelper {
    /**
     * Generates a token with no additional claims.
     *
     * @param subject the token subject
     * @param expiration time until expiration
     * @return signed JWT token
     */
    String generateToken(String subject, Duration expiration);

    /**
     * Generates a token with custom claims.
     *
     * @param subject the token subject
     * @param claims additional JWT claims
     * @param expiration time until expiration
     * @return signed JWT token
     */
    String generateToken(String subject, Map<String, Object> claims, Duration expiration);
}
