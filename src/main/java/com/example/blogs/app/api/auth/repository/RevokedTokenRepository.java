package com.example.blogs.app.api.auth.repository;

import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * Data access for revoked token entities with operations for token validation and cleanup.
 */
public interface RevokedTokenRepository extends JpaRepository<RevokedTokenEntity, Long> {
    /**
     * Checks if a token exists in the revoked tokens table.
     *
     * @param token hashed token to check
     * @return true if token is revoked, false otherwise
     */
    boolean existsByToken(String token);

    /**
     * Deletes all revoked tokens that expired before the specified timestamp.
     * Used by scheduled cleanup tasks to prevent unbounded table growth.
     *
     * @param now cutoff timestamp for deletion
     */
    void deleteByExpiresAtBefore(LocalDateTime now);
}
