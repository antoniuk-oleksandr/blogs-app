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
        UserEntity mockUser = UserEntity.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("hashedpassword")
                .email("test@gmail.com")
                .build();

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

    private CreateUserCommand createTestUserCommand() {
        return new CreateUserCommand(
                "testuser",
                "hashedpassword",
                "test@gmail.com"
        );
    }
}
