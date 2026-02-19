package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.user.dto.UpdateUserRequestDTO;
import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.exception.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Manages user lifecycle operations.
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * @param command user creation details
     * @return the persisted user entity
     * @throws UsernameTakenException if username already exists
     * @throws EmailTakenException    if email already exists
     * @throws FailedToCreateUserException     for other persistence failures
     */
    UserEntity createUser(CreateUserCommand command);

    /**
     * Retrieves a user by username or email address.
     *
     * @param usernameOrEmail the username or email to search for
     * @return the matching user entity
     * @throws UserNotFoundException     if no user is found
     * @throws FailedToFindUserException for database errors
     */
    UserEntity getUserByUsernameOrEmail(String usernameOrEmail);

    /**
     * Retrieves complete user profile with posts by username.
     *
     * @param username the username to search for
     * @return user data transfer object with profile information and post summaries
     * @throws UserNotFoundException     if no user is found
     * @throws FailedToFindUserException for database errors
     */
    UserDTO getUserByUsername(String username);

    UpdateUserResponseDTO updateUserProfile(Long id, UpdateUserRequestDTO requestDTO, MultipartFile profilePicture);
}
