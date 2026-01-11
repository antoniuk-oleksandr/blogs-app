package com.example.blogs.app.api.auth.repository;

import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import com.example.blogs.app.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RevokedTokenRepositoryTest extends AbstractPostgresTest {
    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    @Test
    void existsByToken_shouldReturnTrue_whenTokenExists() {
        RevokedTokenEntity entity = createRevokedToken();
        revokedTokenRepository.save(entity);

        boolean exists = revokedTokenRepository.existsByToken("token");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByToken_shouldReturnFalse_whenTokenDoesNotExist() {
        RevokedTokenEntity entity = createRevokedToken();
        revokedTokenRepository.save(entity);

        boolean exists = revokedTokenRepository.existsByToken("nonexistent-token");

        assertThat(exists).isFalse();
    }

    @Test
    void deleteByExpiresAtBefore_shouldDeleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        RevokedTokenEntity expiredToken = RevokedTokenEntity.builder()
                .token("expired-token")
                .expiresAt(now.minusDays(1))
                .build();
        RevokedTokenEntity validToken = RevokedTokenEntity.builder()
                .token("valid-token")
                .expiresAt(now.plusDays(1))
                .build();
        revokedTokenRepository.save(expiredToken);
        revokedTokenRepository.save(validToken);

        revokedTokenRepository.deleteByExpiresAtBefore(now);

        boolean expiredExists = revokedTokenRepository.existsByToken("expired-token");
        boolean validExists = revokedTokenRepository.existsByToken("valid-token");

        assertThat(expiredExists).isFalse();
        assertThat(validExists).isTrue();
    }

    private RevokedTokenEntity createRevokedToken() {
        return RevokedTokenEntity.builder()
                .token("token")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
    }
}
