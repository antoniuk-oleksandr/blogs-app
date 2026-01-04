package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.*;
import com.example.blogs.app.api.auth.exception.InvalidCredentialsException;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Orchestrates user authentication operations by coordinating password hashing, user validation, and token generation.
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final TokenPairGenerator tokenPairGenerator;

    @Override
    public TokenPair register(RegisterRequest registerRequest) {
        String passwordHash = passwordEncoder.encode(registerRequest.password());

        CreateUserCommand command = new CreateUserCommand(
                registerRequest.username(),
                passwordHash,
                registerRequest.email()
        );

        UserEntity user = userService.createUser(command);

        return tokenPairGenerator.generateTokens(user);
    }

    @Override
    public TokenPair login(LoginRequest loginRequest) {
        UserEntity user;

        try {
            user = userService.findUserByUsernameOrEmail(loginRequest.usernameOrEmail());
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }

        boolean matches = passwordEncoder.matches(loginRequest.password(), user.getPasswordHash());
        if (!matches) {
            throw new InvalidCredentialsException();
        }

        return tokenPairGenerator.generateTokens(user);
    }
}
