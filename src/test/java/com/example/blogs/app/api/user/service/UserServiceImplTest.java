package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.service.PostService;
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
    private PostService postService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepositoryAdapter, postService, userMapper);
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        UserEntity mockUser = createTestUser();

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

    @Test
    void getUserByUsernameOrEmail_shouldReturnUserByUsernameSuccessfully() {
        UserEntity mockUser = createTestUser();

        when(userRepositoryAdapter.findByUsernameOrEmail(anyString())).thenReturn(mockUser);

        UserEntity actualUser = userService.getUserByUsernameOrEmail("testuser");

        assertThat(actualUser.getUsername()).isEqualTo("testuser");
        assertThat(actualUser.getEmail()).isEqualTo("test@gmail.com");
        assertThat(actualUser.getPasswordHash()).isEqualTo("hashedpassword");
        verify(userRepositoryAdapter).findByUsernameOrEmail("testuser");
    }

    @Test
    void getUserByUsername_shouldReturnUserDTOSuccessfully() {
        UserEntity mockUser = createTestUser();
        List<PostEntity> mockPosts = List.of(
                new PostEntity(), new PostEntity()
        );
        List<UserPostSummaryDTO> mockPostSummaries = List.of(
                UserPostSummaryDTO.builder().build(),
                UserPostSummaryDTO.builder().build()
        );
        UserDTO mockUserDTO = UserDTO.builder()
                .username("testuser")
                .bio(null)
                .profilePictureUrl(null)
                .posts(mockPostSummaries)
                .build();
        when(postService.getPostsByUserId(anyLong())).thenReturn(mockPosts);
        when(userRepositoryAdapter.findByUsername(anyString())).thenReturn(mockUser);
        when(userMapper.toUserDTO(any(UserEntity.class), anyList())).thenReturn(mockUserDTO);

        UserDTO actualUser = userService.getUserByUsername("testuser");

        assertThat(actualUser.username()).isEqualTo("testuser");
        assertThat(actualUser.bio()).isNull();
        assertThat(actualUser.profilePictureUrl()).isNull();
        assertThat(actualUser.posts()).hasSize(2);
        verify(userRepositoryAdapter).findByUsername("testuser");
        verify(postService).getPostsByUserId(mockUser.getId());
        verify(userMapper).toUserDTO(any(UserEntity.class), anyList());
    }

    UserEntity createTestUser() {
        return UserEntity.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("hashedpassword")
                .email("test@gmail.com")
                .build();
    }
}
