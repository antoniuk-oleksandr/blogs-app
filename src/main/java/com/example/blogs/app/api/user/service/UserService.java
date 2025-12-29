package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;

/**
 * Manages user lifecycle operations.
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * @param command user creation details
     * @return the persisted user entity
     * @throws com.example.blogs.app.api.user.exception.UsernameTakenException if username already exists
     * @throws com.example.blogs.app.api.user.exception.EmailTakenException    if email already exists
     * @throws com.example.blogs.app.api.user.exception.FailedToCreateUser     for other persistence failures
     */
    UserEntity createUser(CreateUserCommand command);
}
