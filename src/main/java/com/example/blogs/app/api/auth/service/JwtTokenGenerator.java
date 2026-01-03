package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.user.entity.UserEntity;

import java.util.Map;

/**
 * Generates JWT token pairs (access and refresh) with user claims for authentication.
 */
public interface JwtTokenGenerator {
    /**
     * Generates a complete token pair containing access and refresh tokens for the user.
     *
     * @param user the user entity to generate tokens for
     * @return token pair with access and refresh tokens
     */
    TokenPair generateTokens(UserEntity user);

    /**
     * Creates JWT claims map from user entity data with token type.
     *
     * @param user the user entity to extract claims from
     * @param type the token type ("access" or "refresh")
     * @return map of claims including username, email, profilePictureUrl, and type
     */
    Map<String, Object> createClaims(UserEntity user, String type);
}
