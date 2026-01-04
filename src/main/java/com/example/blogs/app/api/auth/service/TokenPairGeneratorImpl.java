package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Creates JWT token pairs with embedded user claims for access and refresh authentication flows.
 */
@Component
@AllArgsConstructor
public class JWTTokenGeneratorImpl implements JWTTokenGenerator {

    private final JWTService jwtService;

    @Override
    public TokenPair generateTokens(UserEntity user) {
        Map<String, Object> accessClaims = createClaims(user, "access");
        Map<String, Object> refreshClaims = createClaims(user, "refresh");

        String accessToken = jwtService.generateAccessToken(user.getId().toString(), accessClaims);
        String refreshToken = jwtService.generateRefreshToken(user.getId().toString(), refreshClaims);

        return new TokenPair(accessToken, refreshToken);
    }

    @Override
    public Map<String, Object> createClaims(UserEntity user, String type) {
        String profilePictureUrl = user.getProfilePictureUrl() != null ? user.getProfilePictureUrl() : "";

        return Map.ofEntries(
                Map.entry("username", user.getUsername()),
                Map.entry("email", user.getEmail()),
                Map.entry("profilePictureUrl", profilePictureUrl),
                Map.entry("type", type)
        );
    }
}
