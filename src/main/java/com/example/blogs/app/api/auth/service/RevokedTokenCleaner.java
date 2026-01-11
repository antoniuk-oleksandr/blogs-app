package com.example.blogs.app.api.auth.service;

/**
 * Scheduled task for removing expired tokens from the revoked tokens table.
 * Prevents unbounded growth by cleaning up tokens that have passed their expiration time.
 */
public interface RevokedTokenCleaner {
    /**
     * Deletes all revoked tokens with expiration timestamps before the current time.
     * Executed on a configurable cron schedule.
     */
    void cleanUpExpiredTokens();
}
