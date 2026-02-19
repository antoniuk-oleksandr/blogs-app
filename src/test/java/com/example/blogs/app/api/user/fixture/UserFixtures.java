package com.example.blogs.app.api.user.fixture;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.dto.UpdateUserRequestDTO;
import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDTO;
import com.example.blogs.app.api.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test fixture factory for creating user entities with predefined values.
 */
public class UserFixtures {

    /**
     * Creates a user entity with default values for all fields.
     *
     * @param id         the user ID
     * @param fileEntity the associated file entity for the user's profile picture
     * @param time       the creation and update timestamp
     * @return configured user entity
     */
    public static UserEntity user(Long id, FileEntity fileEntity, LocalDateTime time) {
        return UserEntity.builder()
                .id(id)
                .username("username")
                .email("email@gmail.com")
                .passwordHash("passwordHash")
                .bio("bio")
                .createdAt(time)
                .updatedAt(time)
                .file(fileEntity)
                .build();
    }

    /**
     * Creates a user entity with default values for all fields except ID and timestamps.
     *
     * @param fileEntity the associated file entity for the user's profile picture
     * @return configured user entity without ID and timestamps
     */
    public static UserEntity user(FileEntity fileEntity) {
        return UserEntity.builder()
                .username("username")
                .email("email@gmail.com")
                .passwordHash("passwordHash")
                .bio("bio")
                .file(fileEntity)
                .build();
    }

    /**
     * Creates a create user command with default values for all fields.
     *
     * @return configured create user command
     */
    public static CreateUserCommand createUserCommand() {
        return new CreateUserCommand(
                "username",
                "passwordHash",
                "email"
        );
    }

    /**
     * Creates a user DTO with default values for all fields, including the provided list of post summaries.
     *
     * @param posts the list of post summaries to include in the user DTO
     * @return configured user DTO with post summaries
     */
    public static UserDTO userDTO(List<UserPostSummaryDTO> posts) {
        return new UserDTO(
                "username",
                "bio",
                "profilePictureUrl",
                posts
        );
    }

    /**
     * Creates an update user request DTO with new values for all fields.
     *
     * @return configured update user request DTO
     */
    public static UpdateUserRequestDTO updateUserRequestDTO() {
        return new UpdateUserRequestDTO(
                "newemail@gmail.com",
                "Updated bio",
                "newPassword",
                "newusername"
        );
    }

    /**
     * Creates an update user response DTO with the specified values.
     *
     * @param username           the username
     * @param bio                the user bio
     * @param profilePictureUrl  the profile picture URL
     * @return configured update user response DTO
     */
    public static UpdateUserResponseDTO updateUserResponseDTO(
            String username, String bio, String profilePictureUrl
    ) {
        return UpdateUserResponseDTO.builder()
                .username(username)
                .bio(bio)
                .profilePictureUrl(profilePictureUrl)
                .build();
    }
}
