package com.example.blogs.app.api.auth.repository.adapter;

import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;

import java.time.LocalDateTime;

/**
 * Adapter for revoked token operations with domain-specific exception handling.
 * Translates database constraint violations into business exceptions.
 */
public interface RevokedTokenRepositoryAdapter {
    /**
     * Persists a revoked token with its expiration timestamp.
     *
     * @param token hashed refresh token to revoke
     * @param expiresAt token expiration timestamp
     * @return persisted revoked token entity
     * @throws com.example.blogs.app.api.auth.exception.TokenAlreadyRevokedException if token was already revoked
     * @throws com.example.blogs.app.api.auth.exception.FailedToRevokeTokenExecption if persistence fails
     */
    RevokedTokenEntity saveRevokedToken(String token, LocalDateTime expiresAt);

    /**
     * Checks if a token has been revoked.
     *
     * @param token hashed refresh token to check
     * @return true if token exists in revoked tokens table
     * @throws com.example.blogs.app.api.auth.exception.FailedToCheckTokenRevokedException if check fails
     */
    boolean isTokenRevoked(String token);

    /**
     * Removes all revoked tokens that expired before the specified timestamp.
     *
     * @param now cutoff timestamp for deletion
     * @throws com.example.blogs.app.api.auth.exception.FailedToCleanRevokedTokensException if cleanup fails
     */
    void deleteExpiredTokens(LocalDateTime now);
}
