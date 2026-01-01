package com.example.blogs.app.api.user.repository.adapter;

import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;

/**
 * Abstracts user repository operations with exception translation for domain-specific errors.
 */
public interface UserRepositoryAdapter {
    /**
     * Persists a new user entity.
     *
     * @param command user creation details
     * @return the saved user entity
     * @throws com.example.blogs.app.api.user.exception.UsernameTakenException if username already exists
     * @throws com.example.blogs.app.api.user.exception.EmailTakenException if email already exists
     * @throws com.example.blogs.app.api.user.exception.FailedToCreateUser for other persistence failures
     */
    UserEntity save(CreateUserCommand command);

    /**
     * Finds a user by username or email address.
     *
     * @param usernameOrEmail the username or email to search for
     * @return the matching user entity
     * @throws com.example.blogs.app.api.user.exception.UserNotFoundException if no user is found
     * @throws com.example.blogs.app.api.user.exception.FailedToFindUserException for database errors
     */
    UserEntity findByUsernameOrEmail(String usernameOrEmail);
}
