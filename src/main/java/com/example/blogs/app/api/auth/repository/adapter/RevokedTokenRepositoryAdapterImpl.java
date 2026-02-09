package com.example.blogs.app.api.auth.repository.adapter;

import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import com.example.blogs.app.api.auth.exception.FailedToCheckTokenRevokedException;
import com.example.blogs.app.api.auth.exception.FailedToCleanRevokedTokensException;
import com.example.blogs.app.api.auth.exception.FailedToRevokeTokenException;
import com.example.blogs.app.api.auth.exception.TokenAlreadyRevokedException;
import com.example.blogs.app.api.auth.repository.RevokedTokenRepository;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.util.SqlExceptionUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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

    private static final Logger log = LoggerFactory.getLogger(RevokedTokenRepositoryAdapterImpl.class);

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
                log.warn("token_revocation_failed reason=already_revoked requestId={}",
                        MDC.get(MDCKeys.REQUEST_ID));
                throw new TokenAlreadyRevokedException(e);
            }

            log.error("database_operation_failed operation=saveRevokedToken error={} requestId={}",
                    e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToRevokeTokenException(e);
        }
    }

    @Override
    public boolean isTokenRevoked(String token) {
        try {
            return revokedTokenJpaRepository.existsByToken(token);
        } catch (Exception e) {
            log.error("database_operation_failed operation=isTokenRevoked error={} requestId={}",
                    e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToCheckTokenRevokedException(e);
        }
    }

    @Override
    @Transactional
    public void deleteExpiredTokens(LocalDateTime now) {
        try {
            revokedTokenJpaRepository.deleteByExpiresAtBefore(now);
        } catch (Exception e) {
            log.error("database_operation_failed operation=deleteExpiredTokens error={} requestId={}",
                    e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToCleanRevokedTokensException(e);
        }
    }
}
