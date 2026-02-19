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

    /**
     * Updates user profile with optional field updates and profile picture replacement.
     * Supports partial updates - only non-null fields are modified.
     * If a new profile picture is provided, the old one is deleted after successful update.
     *
     * @param id             the user ID to update
     * @param requestDTO     DTO containing fields to update (all fields optional)
     * @param profilePicture optional new profile picture file
     * @return response DTO with updated profile information
     * @throws UserNotFoundException       if user with given ID is not found
     * @throws UsernameTakenException      if new username is already taken
     * @throws EmailTakenException         if new email is already taken
     * @throws FailedToUpdateUserException for database update failures
     */
    UpdateUserResponseDTO updateUserProfile(Long id, UpdateUserRequestDTO requestDTO, MultipartFile profilePicture);
}
