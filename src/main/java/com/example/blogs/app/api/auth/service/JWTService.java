package com.example.blogs.app.api.auth.service;

/**
 * Generates JWT tokens with preconfigured expiration times for access and refresh flows.
 */
public interface JWTService {
    /**
     * Generates a refresh token with extended expiration.
     *
     * @param username the subject of the token
     * @return signed JWT refresh token
     */
    String generateRefreshToken(String username);

    /**
     * Generates an access token with short-lived expiration.
     *
     * @param username the subject of the token
     * @return signed JWT access token
     */
    String generateAccessToken(String username);
}
