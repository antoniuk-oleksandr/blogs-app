package com.example.blogs.app.api.auth.repository.adapter;

import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;

import java.time.LocalDateTime;

public interface RevokedTokenRepositoryAdapter {
    RevokedTokenEntity saveRevokedToken(String token, LocalDateTime expiresAt);

    boolean isTokenRevoked(String token);

    void deleteExpiredTokens(LocalDateTime now);
}
