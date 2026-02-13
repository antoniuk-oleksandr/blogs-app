package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDTO;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.mapper.UserMapper;
import com.example.blogs.app.api.user.repository.adapter.UserRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepositoryAdapter, postRepositoryAdapter, userMapper);
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        String username = "username";
        UserEntity mockUser = createTestUser();

        when(userRepositoryAdapter.save(any(CreateUserCommand.class))).thenReturn(mockUser);

        CreateUserCommand command = new CreateUserCommand(
                username,
                "hashedpassword",
                "test@gmail.com"
        );
        UserEntity actualUser = userService.createUser(command);

        assertThat(actualUser.getUsername()).isEqualTo(username);
        assertThat(actualUser.getEmail()).isEqualTo("test@gmail.com");
        assertThat(actualUser.getPasswordHash()).isEqualTo("hashedpassword");
        verify(userRepositoryAdapter).save(any(CreateUserCommand.class));
    }

    @Test
    void getUserByUsernameOrEmail_shouldReturnUserByUsernameSuccessfully() {
        String username = "username";
        UserEntity mockUser = createTestUser();

        when(userRepositoryAdapter.findByUsernameOrEmail(anyString())).thenReturn(mockUser);

        UserEntity actualUser = userService.getUserByUsernameOrEmail(username);

        assertThat(actualUser.getUsername()).isEqualTo(username);
        assertThat(actualUser.getEmail()).isEqualTo("test@gmail.com");
        assertThat(actualUser.getPasswordHash()).isEqualTo("hashedpassword");
        verify(userRepositoryAdapter).findByUsernameOrEmail(username);
    }

    @Test
    void getUserByUsername_shouldReturnUserDTOSuccessfully() {
        String username = "username";
        UserEntity mockUser = createTestUser();
        List<PostEntity> mockPosts = List.of(
                new PostEntity(), new PostEntity()
        );
        List<UserPostSummaryDTO> mockPostSummaries = List.of(
                UserPostSummaryDTO.builder().build(),
                UserPostSummaryDTO.builder().build()
        );
        UserDTO mockUserDTO = UserDTO.builder()
                .username(username)
                .bio(null)
                .profilePictureUrl(null)
                .posts(mockPostSummaries)
                .build();
        when(postRepositoryAdapter.findByAuthorId(anyLong())).thenReturn(mockPosts);
        when(userRepositoryAdapter.findByUsername(anyString())).thenReturn(mockUser);
        when(userMapper.toUserDTO(any(UserEntity.class), anyList())).thenReturn(mockUserDTO);

        UserDTO actualUser = userService.getUserByUsername(username);

        assertThat(actualUser.username()).isEqualTo(username);
        assertThat(actualUser.bio()).isNull();
        assertThat(actualUser.profilePictureUrl()).isNull();
        assertThat(actualUser.posts()).hasSize(2);
        verify(userRepositoryAdapter).findByUsername(username);
        verify(postRepositoryAdapter).findByAuthorId(mockUser.getId());
        verify(userMapper).toUserDTO(any(UserEntity.class), anyList());
    }

    UserEntity createTestUser() {
        return UserEntity.builder()
                .id(1L)
                .username("username")
                .passwordHash("hashedpassword")
                .email("test@gmail.com")
                .build();
    }
}
