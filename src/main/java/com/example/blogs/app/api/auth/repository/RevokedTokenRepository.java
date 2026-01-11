package com.example.blogs.app.api.auth.repository;

import com.example.blogs.app.api.auth.entity.RevokedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RevokedTokenRepository extends JpaRepository<RevokedTokenEntity, Long> {
    boolean existsByToken(String token);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
