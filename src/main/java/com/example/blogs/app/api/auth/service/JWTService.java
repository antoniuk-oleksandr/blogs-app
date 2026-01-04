package com.example.blogs.app.api.auth.service;

import java.util.Map;

/**
 * Generates JWT tokens with preconfigured expiration times for access and refresh flows.
 */
public interface JWTService {
    /**
     * Generates a refresh token with extended expiration.
     *
     * @param subject the subject of the token
     * @return signed JWT refresh token
     */
    String generateRefreshToken(String subject);

    /**
     * Generates a refresh token with extended expiration.
     *
     * @param subject the subject of the token
     * @param claims  additional claims to include in the token
     * @return signed JWT access token
     */
    String generateRefreshToken(String subject, Map<String, Object> claims);

    /**
     * Generates an access token with short-lived expiration.
     *
     * @param subject the subject of the token
     * @return signed JWT access token
     */
    String generateAccessToken(String subject);

    /**
     * Generates an access token with short-lived expiration.
     *
     * @param subject the subject of the token
     * @param claims  additional claims to include in the token
     * @return signed JWT access token
     */
    String generateAccessToken(String subject, Map<String, Object> claims);

    /**
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * @param token the JWT token to parse
     * @return map of claims extracted from the token
     */
    Map<String, Object> parseClaims(String token);
}
