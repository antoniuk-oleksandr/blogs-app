package com.example.blogs.app.api.auth.service;

import com.example.blogs.app.api.auth.dto.RegisterRequest;
import com.example.blogs.app.api.auth.dto.TokenPair;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Orchestrates user registration by coordinating password hashing, user creation, and token generation.
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    @Override
    public TokenPair register(RegisterRequest registerRequest) {
        String passwordHash = passwordEncoder.encode(registerRequest.password());

        CreateUserCommand command = new CreateUserCommand(
                registerRequest.username(),
                passwordHash,
                registerRequest.email()
        );

        UserEntity user = userService.createUser(command);

        return new TokenPair(
                jwtService.generateAccessToken(user.getUsername()),
                jwtService.generateRefreshToken(user.getUsername())
        );
    }
}
