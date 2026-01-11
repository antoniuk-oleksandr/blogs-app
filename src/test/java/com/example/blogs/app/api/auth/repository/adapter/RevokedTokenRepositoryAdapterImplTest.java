package com.example.blogs.app.api.auth.repository.adapter;

import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import com.example.blogs.app.api.auth.exception.FailedToCheckTokenRevokedException;
import com.example.blogs.app.api.auth.exception.FailedToCleanRevokedTokensException;
import com.example.blogs.app.api.auth.exception.FailedToRevokeTokenExecption;
import com.example.blogs.app.api.auth.exception.TokenAlreadyRevokedException;
import com.example.blogs.app.api.auth.repository.RevokedTokenRepository;
import com.example.blogs.app.util.SqlExceptionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RevokedTokenRepositoryAdapterImplTest {

    @Mock
    private RevokedTokenRepository revokedTokenJpaRepository;

    @Mock
    private SqlExceptionUtils sqlExceptionUtils;

    private RevokedTokenRepositoryAdapter revokedTokenRepositoryAdapter;

    @BeforeEach
    void setUp() {
        revokedTokenRepositoryAdapter = new RevokedTokenRepositoryAdapterImpl(
                revokedTokenJpaRepository,
                sqlExceptionUtils
        );
    }

    @Test
    void saveRevokedToken_shouldSaveTokenSuccessfully() {
        RevokedTokenEntity mockedEntity = RevokedTokenEntity.builder()
                .token("sampleToken")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();

        when(revokedTokenJpaRepository.save(any(RevokedTokenEntity.class)))
                .thenReturn(mockedEntity);

        RevokedTokenEntity result = revokedTokenRepositoryAdapter.saveRevokedToken(
                "sampleToken",
                mockedEntity.getExpiresAt()
        );

        assertThat(result).isEqualTo(mockedEntity);
        verify(revokedTokenJpaRepository).save(any(RevokedTokenEntity.class));
    }

    @Test
    void saveRevokedToken_shouldThrowTokenAlreadyRevokedException_whenUniqueViolationOccurs() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        when(revokedTokenJpaRepository.save(any(RevokedTokenEntity.class)))
                .thenThrow(new RuntimeException("DB error"));
        when(sqlExceptionUtils.containsUniqueViolation(any(Exception.class), anyString()))
                .thenReturn(true);

        assertThatThrownBy(() -> revokedTokenRepositoryAdapter.saveRevokedToken(
                "sampleToken",
                expiresAt
        )).isInstanceOf(TokenAlreadyRevokedException.class);

        verify(revokedTokenJpaRepository).save(any(RevokedTokenEntity.class));
        verify(sqlExceptionUtils).containsUniqueViolation(any(Exception.class), eq("token"));
    }

    @Test
    void saveRevokedToken_shouldThrowFailedToRevokeTokenException_whenOtherExceptionOccurs() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        when(revokedTokenJpaRepository.save(any(RevokedTokenEntity.class)))
                .thenThrow(new RuntimeException("DB error"));
        when(sqlExceptionUtils.containsUniqueViolation(any(Exception.class), anyString()))
                .thenReturn(false);

        assertThatThrownBy(() -> revokedTokenRepositoryAdapter.saveRevokedToken(
                "sampleToken",
                expiresAt
        )).isInstanceOf(FailedToRevokeTokenExecption.class);

        verify(revokedTokenJpaRepository).save(any(RevokedTokenEntity.class));
        verify(sqlExceptionUtils).containsUniqueViolation(any(Exception.class), eq("token"));
    }

    @Test
    void isTokenRevoked_shouldReturnTrue_whenTokenExists() {
        when(revokedTokenJpaRepository.existsByToken(anyString())).thenReturn(true);

        boolean result = revokedTokenRepositoryAdapter.isTokenRevoked("sampleToken");

        assertThat(result).isTrue();
        verify(revokedTokenJpaRepository).existsByToken("sampleToken");
    }

    @Test
    void isTokenRevoked_shouldReturnFalse_whenTokenDoesNotExist() {
        when(revokedTokenJpaRepository.existsByToken(anyString())).thenReturn(false);

        boolean result = revokedTokenRepositoryAdapter.isTokenRevoked("sampleToken");

        assertThat(result).isFalse();
        verify(revokedTokenJpaRepository).existsByToken("sampleToken");
    }

    @Test
    void isTokenRevoked_shouldThrowFailedToCheckTokenRevokedException_whenExceptionOccurs() {
        when(revokedTokenJpaRepository.existsByToken(anyString()))
                .thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> revokedTokenRepositoryAdapter.isTokenRevoked("sampleToken"))
                .isInstanceOf(FailedToCheckTokenRevokedException.class);
        verify(revokedTokenJpaRepository).existsByToken("sampleToken");
    }

    @Test
    void deleteExpiredTokens_shouldDeleteExpiredTokensSuccessfully() {
        LocalDateTime now = LocalDateTime.now();

        revokedTokenRepositoryAdapter.deleteExpiredTokens(now);

        verify(revokedTokenJpaRepository, times(1))
                .deleteByExpiresAtBefore(now);
        verify(revokedTokenJpaRepository).deleteByExpiresAtBefore(now);
    }

    @Test
    void deleteExpiredTokens_shouldThrowFailedToCleanRevokedTokensException_whenExceptionOccurs() {
        LocalDateTime now = LocalDateTime.now();
        doThrow(new RuntimeException("DB error")).when(revokedTokenJpaRepository)
                .deleteByExpiresAtBefore(now);

        assertThatThrownBy(() -> revokedTokenRepositoryAdapter.deleteExpiredTokens(now))
                .isInstanceOf(FailedToCleanRevokedTokensException.class);
        verify(revokedTokenJpaRepository, times(1))
                .deleteByExpiresAtBefore(now);
        verify(revokedTokenJpaRepository).deleteByExpiresAtBefore(now);
    }
}
