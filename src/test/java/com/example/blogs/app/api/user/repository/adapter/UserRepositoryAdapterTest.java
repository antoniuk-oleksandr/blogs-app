package com.example.blogs.app.api.user.repository.adapter;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.exception.*;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import com.example.blogs.app.api.user.repository.UserRepository;
import com.example.blogs.app.util.SqlExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SqlExceptionUtils sqlExceptionUtils;

    private UserRepositoryAdapter userRepositoryAdapter;

    @BeforeEach
    void setUp() {
        userRepositoryAdapter = new UserRepositoryAdapterImpl(userRepository, sqlExceptionUtils);
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(userId, null, now);

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        CreateUserCommand command = UserFixtures.createUserCommand();
        UserEntity actualUser = userRepositoryAdapter.save(command);

        assertThat(actualUser).isEqualTo(mockUser);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_shouldThrowExceptionWhenUsernameExists() {
        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(new RuntimeException("Database failed"));
        when(sqlExceptionUtils.containsUniqueViolation(any(), eq("users_username_key")))
                .thenReturn(true);

        CreateUserCommand command = UserFixtures.createUserCommand();

        assertThatThrownBy(() -> userRepositoryAdapter.save(command))
                .isInstanceOf(UsernameTakenException.class);
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailExists() {
        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(new RuntimeException("Database failed"));
        when(sqlExceptionUtils.containsUniqueViolation(any(), eq("users_username_key")))
                .thenReturn(false);
        when(sqlExceptionUtils.containsUniqueViolation(any(), eq("users_email_key")))
                .thenReturn(true);

        CreateUserCommand command = UserFixtures.createUserCommand();

        assertThatThrownBy(() -> userRepositoryAdapter.save(command))
                .isInstanceOf(EmailTakenException.class);
    }

    @Test
    void createUser_shouldThrowGenericExceptionForOtherDataAccessIssues() {
        DataAccessException exception = new DataIntegrityViolationException("generic data access issue");

        when(userRepository
                .save(any(UserEntity.class)))
                .thenThrow(exception);

        CreateUserCommand command = UserFixtures.createUserCommand();

        assertThatThrownBy(() -> userRepositoryAdapter.save(command))
                .isInstanceOf(FailedToCreateUserException.class);
    }

    @Test
    void createUser_shouldThrowFailedToCreateUserForOtherConstraintViolations() {
        DataAccessException exception = new DataIntegrityViolationException(
                "could not execute statement",
                new ConstraintViolationException("simulated", null, "some_other_constraint")
        );

        when(userRepository.save(any(UserEntity.class))).thenThrow(exception);

        CreateUserCommand command = UserFixtures.createUserCommand();


        assertThatThrownBy(() -> userRepositoryAdapter.save(command))
                .isInstanceOf(FailedToCreateUserException.class);
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUserByUsername_whenUserExists() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(userId, null, now);

        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(mockUser));

        UserEntity user = userRepositoryAdapter.findByUsernameOrEmail(mockUser.getUsername());

        assertThat(user.getUsername()).isEqualTo(mockUser.getUsername());
        assertThat(user.getEmail()).isEqualTo(mockUser.getEmail());
        assertThat(user.getPasswordHash()).isEqualTo(mockUser.getPasswordHash());
        verify(userRepository).findUserByUsernameOrEmail(mockUser.getUsername(), mockUser.getUsername());
    }

    @Test
    void findByUsernameOrEmail_shouldReturnUserByEmail_whenUserExists() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(userId, null, now);

        when(userRepository.findUserByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(mockUser));

        UserEntity user = userRepositoryAdapter.findByUsernameOrEmail(mockUser.getEmail());

        assertThat(user.getUsername()).isEqualTo(mockUser.getUsername());
        assertThat(user.getEmail()).isEqualTo(mockUser.getEmail());
        assertThat(user.getPasswordHash()).isEqualTo(mockUser.getPasswordHash());
        verify(userRepository).findUserByUsernameOrEmail(mockUser.getEmail(), mockUser.getEmail());
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

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(userId, null, now);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(mockUser));

        UserEntity user = userRepositoryAdapter.findByUsername(mockUser.getUsername());

        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getUsername()).isEqualTo(mockUser.getUsername());
        assertThat(user.getPasswordHash()).isEqualTo(mockUser.getPasswordHash());
        assertThat(user.getEmail()).isEqualTo(mockUser.getEmail());
        verify(userRepository).findByUsername(mockUser.getUsername());
    }

    @Test
    void findByUsername_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRepositoryAdapter.findByUsername("nonexistentuser"))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByUsername("nonexistentuser");
    }

    @Test
    void findByUsername_shouldThrowFailedToFindUserException_whenDataAccessExceptionOccurs() {
        DataAccessException exception = new DataIntegrityViolationException("generic data access issue");
        when(userRepository.findByUsername(anyString()))
                .thenThrow(exception);

        assertThatThrownBy(() -> userRepositoryAdapter.findByUsername("testuser"))
                .isInstanceOf(FailedToFindUserException.class);

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        Long fileId = 1L;
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(fileId, now);
        UserEntity mockUser = UserFixtures.user(userId, mockFile, now);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(mockUser));

        UserEntity user = userRepositoryAdapter.findById(1L);
        assertThat(user).isEqualTo(mockUser);
    }

    @Test
    void findById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRepositoryAdapter.findById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(userId);
    }

    @Test
    void findById_shouldThrowFailedToFindUserException_whenDatabaseFails() {
        Long userId = 1L;
        when(userRepository.findById(anyLong()))
                .thenThrow(new RuntimeException("Database fail"));

        assertThatThrownBy(() -> userRepositoryAdapter.findById(userId))
                .isInstanceOf(FailedToFindUserByIdException.class)
                .hasMessage("Failed to find user by ID");

        verify(userRepository).findById(userId);
    }

    @Test
    void update_shouldUpdateUserSuccessfully() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(userId, null, now);

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        UserEntity updatedUser = userRepositoryAdapter.update(mockUser);

        assertThat(updatedUser).isEqualTo(mockUser);
        verify(userRepository).save(mockUser);
    }

    @Test
    void update_shouldThrowFailedToUpdateUserException_whenDatabaseFails() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity mockUser = UserFixtures.user(userId, null, now);

        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(new RuntimeException("Database fail"));

        assertThatThrownBy(() -> userRepositoryAdapter.update(mockUser))
                .isInstanceOf(FailedToUpdateUserException.class)
                .hasMessage("Failed to update user");

        verify(userRepository).save(mockUser);
    }
}
