package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.service.FileService;
import com.example.blogs.app.api.file.service.FileUrlBuilder;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.post.service.TransactionalFileUploader;
import com.example.blogs.app.api.user.dto.UpdateUserRequestDTO;
import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.exception.*;
import com.example.blogs.app.api.user.mapper.UserMapper;
import com.example.blogs.app.api.user.repository.adapter.UserRepositoryAdapter;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.security.Hasher;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Orchestrates user operations by coordinating repository access, post retrieval, and entity-to-DTO mapping.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepositoryAdapter userRepositoryAdapter;

    private final PostRepositoryAdapter postRepositoryAdapter;

    private final UserMapper userMapper;

    private final TransactionalFileUploader transactionalFileUploader;

    private final FileUrlBuilder fileUrlBuilder;

    private final FileService fileService;

    private final Hasher hasher;

    @Override
    public UserEntity createUser(CreateUserCommand command) {
        return userRepositoryAdapter.save(command);
    }

    @Override
    public UserEntity getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepositoryAdapter.findByUsernameOrEmail(usernameOrEmail);
    }

    /**
     * Retrieves complete user profile with posts by username.
     * Fetches user entity, loads associated posts, and maps to DTO.
     *
     * @param username the username to search for
     * @return user data transfer object with profile information and post summaries
     * @throws UserNotFoundException     if no user is found
     * @throws FailedToFindUserException for database errors
     */
    @Override
    public UserDTO getUserByUsername(String username) {
        UserEntity userEntity = userRepositoryAdapter.findByUsername(username);
        List<PostEntity> postEntities = postRepositoryAdapter.findByAuthorId(userEntity.getId());

        String profilePictureUrl = Optional.ofNullable(userEntity.getFile())
                .map(fileUrlBuilder::build)
                .orElse(null);

        log.info("user_profile_viewed userId={} postCount={} requestId={}",
                userEntity.getId(), postEntities.size(), MDC.get(MDCKeys.REQUEST_ID));

        return userMapper.toUserDTO(userEntity, postEntities, profilePictureUrl);
    }

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
    @Override
    @Transactional
    public UpdateUserResponseDTO updateUserProfile(
            Long id, UpdateUserRequestDTO requestDTO, MultipartFile profilePicture
    ) {
        UserEntity user = userRepositoryAdapter.findById(id);
        Optional<FileEntity> oldFile = Optional.ofNullable(user.getFile());

        Optional<FileEntity> newFile = Optional.ofNullable(profilePicture)
                .map(image -> transactionalFileUploader
                        .uploadWithTransactionRollback(image, "users"));
        newFile.ifPresent(user::setFile);

        String profilePictureUrl = newFile
                .map(fileUrlBuilder::build)
                .orElseGet(() -> oldFile.map(fileUrlBuilder::build).orElse(null));

        String passwordHash = null;
        if (requestDTO.password() != null) {
            passwordHash = hasher.hash(requestDTO.password());
        }

        UserEntity userToUpdate = userMapper.toUserEntity(requestDTO, user, passwordHash);

        UserEntity updatedUser = userRepositoryAdapter.update(userToUpdate);

        if (requestDTO.password() != null) {
            log.info("password_updated userId={} requestId={}", id, MDC.get(MDCKeys.REQUEST_ID));
        }

        newFile.ifPresentOrElse(
                newF -> oldFile.ifPresentOrElse(
                        old -> {
                            log.info("profile_picture_replaced userId={} oldFileId={} newFileId={} requestId={}",
                                    id, old.getId(), newF.getId(), MDC.get(MDCKeys.REQUEST_ID));
                            fileService.delete(old);
                        },
                        () -> log.info("profile_picture_added userId={} newFileId={} requestId={}",
                                id, newF.getId(), MDC.get(MDCKeys.REQUEST_ID))
                ),
                () -> oldFile.ifPresent(old -> log.info("profile_picture_unchanged userId={} fileId={} requestId={}",
                        id, old.getId(), MDC.get(MDCKeys.REQUEST_ID)))
        );


        return userMapper.toUpdateUserResponseDTO(updatedUser, profilePictureUrl);
    }
}
