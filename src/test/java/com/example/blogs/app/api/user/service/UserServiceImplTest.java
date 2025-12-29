package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.repository.adapter.UserRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepositoryAdapter);
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        UserEntity mockUser = UserEntity.builder()
                .username("testuser")
                .passwordHash("hashedpassword")
                .email("test@gmail.com")
                .build();

        when(userRepositoryAdapter.save(any(CreateUserCommand.class))).thenReturn(mockUser);

        CreateUserCommand command = new CreateUserCommand(
                "testuser",
                "hashedpassword",
                "test@gmail.com"
        );
        UserEntity actualUser = userService.createUser(command);

        assertThat(actualUser.getUsername()).isEqualTo("testuser");
        assertThat(actualUser.getEmail()).isEqualTo("test@gmail.com");
        assertThat(actualUser.getPasswordHash()).isEqualTo("hashedpassword");
        verify(userRepositoryAdapter).save(any(CreateUserCommand.class));
    }
}
