package com.example.blogs.app.api.auth.repository.adapter;

import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import com.example.blogs.app.api.auth.exception.FailedToCheckTokenRevokedException;
import com.example.blogs.app.api.auth.exception.FailedToCleanRevokedTokensException;
import com.example.blogs.app.api.auth.exception.FailedToRevokeTokenExecption;
import com.example.blogs.app.api.auth.exception.TokenAlreadyRevokedException;
import com.example.blogs.app.api.auth.repository.RevokedTokenRepository;
import com.example.blogs.app.util.SqlExceptionUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Translates database constraint violations and SQL errors into domain-specific exceptions
 * for revoked token operations.
 */
@Component
@AllArgsConstructor
public class RevokedTokenRepositoryAdapterImpl implements RevokedTokenRepositoryAdapter {

    private final RevokedTokenRepository revokedTokenJpaRepository;

    private final SqlExceptionUtils sqlExceptionUtils;

    @Override
    public RevokedTokenEntity saveRevokedToken(String token, LocalDateTime expiresAt) {
        RevokedTokenEntity entity = RevokedTokenEntity.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build();

        try {
            return revokedTokenJpaRepository.save(entity);
        } catch (Exception e) {
            if (sqlExceptionUtils.containsUniqueViolation(e, "token")) {
                throw new TokenAlreadyRevokedException();
            }

            throw new FailedToRevokeTokenExecption();
        }
    }

    @Override
    public boolean isTokenRevoked(String token) {
        try {
            return revokedTokenJpaRepository.existsByToken(token);
        } catch (Exception e) {
            throw new FailedToCheckTokenRevokedException(e);
        }
    }

    @Override
    @Transactional
    public void deleteExpiredTokens(LocalDateTime now) {
        try {
            revokedTokenJpaRepository.deleteByExpiresAtBefore(now);
        } catch (Exception e) {
            throw new FailedToCleanRevokedTokensException(e);
        }
    }
}
