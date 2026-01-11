package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.*;
import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import com.example.blogs.app.api.auth.exception.InvalidCredentialsException;
import com.example.blogs.app.api.auth.exception.UnauthorizedException;

/**
 * Handles authentication operations including user registration, login, token management,
 * and logout functionality.
 */
public interface AuthService {
    /**
     * Registers a new user and returns JWT tokens for immediate authentication.
     *
     * @param registerRequest user registration details
     * @return access and refresh token pair
     * @throws com.example.blogs.app.api.user.exception.UsernameTakenException if username already exists
     * @throws com.example.blogs.app.api.user.exception.EmailTakenException if email already exists
     */
    TokenPair register(RegisterRequest registerRequest);

    /**
     * Authenticates a user and generates JWT tokens.
     *
     * @param loginRequest user login credentials
     * @return access and refresh token pair
     * @throws InvalidCredentialsException if credentials are invalid
     */
    TokenPair login(LoginRequest loginRequest);

    /**
     * Refreshes an access token using a valid refresh token.
     *
     * @param tokenRequest request containing the refresh token
     * @return new access token with updated expiration
     * @throws UnauthorizedException if refresh token is invalid, expired, or revoked
     */
    AccessTokenResponse refreshAccessToken(RefreshTokenRequest tokenRequest);

    /**
     * Revokes a refresh token and invalidates all associated sessions.
     *
     * @param logoutRequest request containing the refresh token to revoke
     * @return persisted revoked token entity
     * @throws com.example.blogs.app.api.auth.exception.TokenAlreadyRevokedException if token was already revoked
     * @throws UnauthorizedException if refresh token is invalid or expired
     */
    RevokedTokenEntity logout(LogoutRequest logoutRequest);
}
