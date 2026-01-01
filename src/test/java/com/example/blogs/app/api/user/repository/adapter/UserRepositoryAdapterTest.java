package com.example.blogs.app.api.user.repository.adapter;

import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.exception.EmailTakenException;
import com.example.blogs.app.api.user.exception.FailedToCreateUser;
import com.example.blogs.app.api.user.exception.UsernameTakenException;
import com.example.blogs.app.api.user.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    private UserRepositoryAdapter userRepositoryAdapter;

    @BeforeEach
    void setUp() {
        userRepositoryAdapter = new UserRepositoryAdapterImpl(userRepository);
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        UserEntity mockUser = createTestUserEntity();

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        CreateUserCommand command = createTestUserCommand();
        UserEntity actualUser = userRepositoryAdapter.save(command);

        assertThat(actualUser.getId()).isEqualTo(1L);
        assertThat(actualUser.getUsername()).isEqualTo("testuser");
        assertThat(actualUser.getEmail()).isEqualTo("test@gmail.com");
        assertThat(actualUser.getPasswordHash()).isEqualTo("hashedpassword");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_shouldThrowExceptionWhenUsernameExists() {
        DataAccessException exception = new DataIntegrityViolationException(
                "could not execute statement",
                new ConstraintViolationException("simulated", null, "users_username_key")
        );

        when(userRepository
                .save(any(UserEntity.class)))
                .thenThrow(exception);

        CreateUserCommand command = createTestUserCommand();
        assertThatThrownBy(() -> userRepositoryAdapter.save(command))
                .isInstanceOf(UsernameTakenException.class);
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailExists() {
        DataAccessException exception = new DataIntegrityViolationException(
                "could not execute statement",
                new ConstraintViolationException("simulated", null, "users_email_key")
        );

        when(userRepository
                .save(any(UserEntity.class)))
                .thenThrow(exception);

        CreateUserCommand command = createTestUserCommand();
        assertThatThrownBy(() -> userRepositoryAdapter.save(command))
                .isInstanceOf(EmailTakenException.class);
    }

    @Test
    void createUser_shouldThrowGenericExceptionForOtherDataAccessIssues() {
        DataAccessException exception = new DataIntegrityViolationException("generic data access issue");

        when(userRepository
                .save(any(UserEntity.class)))
                .thenThrow(exception);

        CreateUserCommand command = createTestUserCommand();
        assertThatThrownBy(() -> userRepositoryAdapter.save(command))
                .isInstanceOf(FailedToCreateUser.class);
    }

    @Test
    void createUser_shouldThrowFailedToCreateUserForOtherConstraintViolations() {
        DataAccessException exception = new DataIntegrityViolationException(
                "could not execute statement",
                new ConstraintViolationException("simulated", null, "some_other_constraint")
        );

        when(userRepository.save(any(UserEntity.class))).thenThrow(exception);

        CreateUserCommand command = createTestUserCommand();

        assertThatThrownBy(() -> userRepositoryAdapter.save(command))
                .isInstanceOf(FailedToCreateUser.class);
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUserByUsername_whenUserExists() {
        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(createTestUserEntity()));

        UserEntity user = userRepositoryAdapter.findByUsernameOrEmail("testuser");

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@gmail.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashedpassword");
        verify(userRepository).findUserByUsernameOrEmail("testuser", "testuser");
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUserByEmail_whenUserExists() {
        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(createTestUserEntity()));

        UserEntity user = userRepositoryAdapter.findByUsernameOrEmail("test@gmail.com");

        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@gmail.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashedpassword");
        verify(userRepository).findUserByUsernameOrEmail("test@gmail.com", "test@gmail.com");
    }

    @Test
    void findByUsernameOrEmail_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRepositoryAdapter.findByUsernameOrEmail("nonexistentuser"))
                .isInstanceOf(com.example.blogs.app.api.user.exception.UserNotFoundException.class);

        verify(userRepository).findUserByUsernameOrEmail("nonexistentuser", "nonexistentuser");
    }

    @Test
    void findByUsernameOrEmail_shouldThrowFailedToFindUserException_whenDataAccessExceptionOccurs() {
        DataAccessException exception = new DataIntegrityViolationException("generic data access issue");

        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString()))
                .thenThrow(exception);

        assertThatThrownBy(() -> userRepositoryAdapter.findByUsernameOrEmail("testuser"))
                .isInstanceOf(com.example.blogs.app.api.user.exception.FailedToFindUserException.class);

        verify(userRepository).findUserByUsernameOrEmail("testuser", "testuser");
    }

    private UserEntity createTestUserEntity() {
        return UserEntity.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("hashedpassword")
                .email("test@gmail.com")
                .build();
    }

    private CreateUserCommand createTestUserCommand() {
        return new CreateUserCommand(
                "testuser",
                "hashedpassword",
                "test@gmail.com"
        );
    }
}
