package com.example.blogs.app.api.auth.service;

public interface RevokedTokenCleaner {
    void cleanUpExpiredTokens();
}
