package com.example.blogs.app.api.auth.service;


import com.example.blogs.app.api.auth.dto.RegisterRequest;
import com.example.blogs.app.api.auth.dto.TokenPair;

/**
 * Handles authentication operations including user registration and token generation.
 */
public interface AuthService {
    /**
     * Registers a new user and returns JWT tokens for immediate authentication.
     *
     * @param registerRequest user registration details
     * @return access and refresh token pair
     */
    TokenPair register(RegisterRequest registerRequest);
}
