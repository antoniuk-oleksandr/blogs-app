package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.exception.FailedToParseClaimsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

/**
 * Delegates to JWTHelper with application-configured expiration durations for access and refresh tokens.
 */
@Service
public class JWTServiceImpl implements JWTService {

    private final JWTHelper jwtHelper;

    private final Duration accessTokenExpiration;

    private final Duration refreshTokenExpiration;

    /**
     * Constructs a JWTServiceImpl with configured dependencies and token expiration durations.
     *
     * @param jwtHelper              helper for generating JWT tokens
     * @param accessTokenExpiration  duration for access token validity
     * @param refreshTokenExpiration duration for refresh token validity
     */
    public JWTServiceImpl(
            JWTHelper jwtHelper,
            @Value("${jwt.access-token-expiration}") Duration accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") Duration refreshTokenExpiration
    ) {
        this.jwtHelper = jwtHelper;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Generates an access token with short-lived expiration.
     *
     * @param subject the subject of the token
     * @param claims  additional claims to include in the token
     * @return signed JWT access token
     */
    @Override
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return jwtHelper.generateToken(subject, claims, accessTokenExpiration);
    }

    /**
     * Generates a refresh token with extended expiration.
     *
     * @param subject the subject of the token
     * @return signed JWT refresh token
     */
    @Override
    public String generateRefreshToken(String subject) {
        return jwtHelper.generateToken(subject, refreshTokenExpiration);
    }

    /**
     * Generates a refresh token with extended expiration.
     *
     * @param subject the subject of the token
     * @param claims  additional claims to include in the token
     * @return signed JWT access token
     */
    @Override
    public String generateRefreshToken(String subject, Map<String, Object> claims) {
        return jwtHelper.generateToken(subject, claims, refreshTokenExpiration);
    }

    /**
     * Generates an access token with short-lived expiration.
     *
     * @param subject the subject of the token
     * @return signed JWT access token
     */
    @Override
    public String generateAccessToken(String subject) {
        return jwtHelper.generateToken(subject, accessTokenExpiration);
    }

    /**
     * Validates a JWT token signature and expiration.
     *
     * @param token the JWT token to validate
     * @return true if token is valid and not expired, false otherwise
     */
    @Override
    public boolean validateToken(String token) {
        return jwtHelper.validateToken(token);
    }

    /**
     * Parses and extracts claims from a JWT token.
     *
     * @param token the JWT token to parse
     * @return map of claims from the token payload
     * @throws FailedToParseClaimsException if token is malformed or signature is invalid
     */
    @Override
    public Map<String, Object> parseClaims(String token) {
        return jwtHelper.parseClaims(token);
    }
}
