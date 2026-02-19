package com.example.blogs.app.api.user.repository.adapter;

import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.exception.*;
import com.example.blogs.app.api.user.repository.UserRepository;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.util.SqlExceptionUtils;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Translates database constraint violations and errors into domain-specific exceptions.
 */
@Component
@AllArgsConstructor
public class UserRepositoryAdapterImpl implements UserRepositoryAdapter {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryAdapterImpl.class);

    private final UserRepository userRepository;

    private final SqlExceptionUtils sqlExceptionUtils;

    @Override
    public UserEntity save(CreateUserCommand command) {
        UserEntity user = UserEntity.builder()
                .username(command.username())
                .passwordHash(command.passwordHash())
                .email(command.email())
                .build();

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            if (sqlExceptionUtils.containsUniqueViolation(e, "users_username_key")) {
                log.warn("user_creation_failed reason=username_taken username={} requestId={}",
                        command.username(), MDC.get(MDCKeys.REQUEST_ID));
                throw new UsernameTakenException(e);
            }
            if (sqlExceptionUtils.containsUniqueViolation(e, "users_email_key")) {
                log.warn("user_creation_failed reason=email_taken email={} requestId={}",
                        command.email(), MDC.get(MDCKeys.REQUEST_ID));
                throw new EmailTakenException(e);
            }

            log.error("database_operation_failed operation=save username={} error={} requestId={}",
                    command.username(), e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToCreateUserException(e);
        }
    }

    @Override
    public UserEntity findByUsernameOrEmail(String usernameOrEmail) {
        try {
            return userRepository.findUserByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                    .orElseThrow(() -> new UserNotFoundException(null));
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("database_operation_failed operation=findByUsernameOrEmail usernameOrEmail={} error={} requestId={}",
                    usernameOrEmail, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToFindUserException(e);
        }
    }

    /**
     * Finds a user by username and translates exceptions to domain-specific errors.
     *
     * @param username the username to search for
     * @return the matching user entity
     * @throws UserNotFoundException     if no user is found
     * @throws FailedToFindUserException for database errors
     */
    @Override
    public UserEntity findByUsername(String username) {
        try {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(null));
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("database_operation_failed operation=findByUsername username={} error={} requestId={}",
                    username, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToFindUserException(e);
        }
    }

    @Override
    public UserEntity findById(Long id) {
        try {
            return userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(null));
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("database_operation_failed operation=findById userId={} error={} requestId={}",
                    id, e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToFindUserByIdException(e);
        }
    }

    /**
     * Updates an existing user entity and translates exceptions to domain-specific errors.
     * Handles uniqueness constraint violations for username and email updates.
     *
     * @param userEntity the user entity to update with new values
     * @return the updated user entity
     * @throws UsernameTakenException      if updated username is already taken
     * @throws EmailTakenException         if updated email is already taken
     * @throws FailedToUpdateUserException for database update failures
     */
    @Override
    public UserEntity update(UserEntity userEntity) {
        try {
            return userRepository.save(userEntity);
        } catch (Exception e) {
            if (e instanceof ConstraintViolationException) {
                if (sqlExceptionUtils.containsUniqueViolation(e, "users_username_key")) {
                    log.warn("user_update_failed reason=username_taken username={} userId={} requestId={}",
                            userEntity.getUsername(), userEntity.getId(), MDC.get(MDCKeys.REQUEST_ID));
                    throw new UsernameTakenException(e);
                }
                if (sqlExceptionUtils.containsUniqueViolation(e, "users_email_key")) {
                    log.warn("user_update_failed reason=email_taken email={} userId={} requestId={}",
                            userEntity.getEmail(), userEntity.getId(), MDC.get(MDCKeys.REQUEST_ID));
                    throw new EmailTakenException(e);
                }
            }

            log.error("database_operation_failed operation=update userId={} error={} requestId={}",
                    userEntity.getId(), e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToUpdateUserException(e);
        }
    }
}
