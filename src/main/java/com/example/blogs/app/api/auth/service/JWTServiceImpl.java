package com.example.blogs.app.api.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

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
     * @param jwtHelper helper for generating JWT tokens
     * @param accessTokenExpiration duration for access token validity
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

    @Override
    public String generateAccessToken(String subject) {
        return jwtHelper.generateToken(subject, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(String subject) {
        return jwtHelper.generateToken(subject, refreshTokenExpiration);
    }
}
