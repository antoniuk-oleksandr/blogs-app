package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.file.service.FileService;
import com.example.blogs.app.api.file.service.FileUrlBuilder;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.post.service.TransactionalFileUploader;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.dto.UpdateUserRequestDTO;
import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDTO;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import com.example.blogs.app.api.user.mapper.UserMapper;
import com.example.blogs.app.api.user.repository.adapter.UserRepositoryAdapter;
import com.example.blogs.app.security.Hasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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


    @Mock
    private TransactionalFileUploader transactionalFileUploader;

    @Mock
    private FileUrlBuilder fileUrlBuilder;

    @Mock
    private FileService fileService;

    @Mock
    private Hasher hasher;

    @Mock
    private MultipartFile multipartFile;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepositoryAdapter,
                postRepositoryAdapter,
                userMapper,
                transactionalFileUploader,
                fileUrlBuilder,
                fileService,
                hasher
        );
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        Long fileId = 1L;
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(fileId, now);
        UserEntity mockUser = UserFixtures.user(userId, mockFile, now);

        when(userRepositoryAdapter.save(any(CreateUserCommand.class))).thenReturn(mockUser);

        CreateUserCommand command = UserFixtures.createUserCommand();
        UserEntity actualUser = userService.createUser(command);

        assertThat(actualUser).isEqualTo(mockUser);
        verify(userRepositoryAdapter).save(any(CreateUserCommand.class));
    }

    @Test
    void getUserByUsernameOrEmail_shouldReturnUserByUsernameSuccessfully() {
        Long fileId = 1L;
        Long userId = 1L;
        String username = "username";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(fileId, now);
        UserEntity mockUser = UserFixtures.user(userId, mockFile, now);

        when(userRepositoryAdapter.findByUsernameOrEmail(anyString())).thenReturn(mockUser);

        UserEntity actualUser = userService.getUserByUsernameOrEmail(username);

        assertThat(actualUser).isEqualTo(mockUser);
        verify(userRepositoryAdapter).findByUsernameOrEmail(username);
    }

    @Test
    void getUserByUsername_shouldReturnUserDTOSuccessfully() {
        Long fileId = 1L;
        Long userId = 1L;
        String username = "username";
        String profilePictureUrl = "profilePictureUrl";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity mockFile = FileFixtures.file(fileId, now);
        UserEntity mockUser = UserFixtures.user(userId, mockFile, now);
        List<PostEntity> mockPosts = List.of(
                new PostEntity(), new PostEntity()
        );
        List<UserPostSummaryDTO> mockPostSummaries = List.of(
                UserPostSummaryDTO.builder().build(),
                UserPostSummaryDTO.builder().build()
        );
        UserDTO mockUserDTO = UserFixtures.userDTO(mockPostSummaries);
        when(postRepositoryAdapter.findByAuthorId(anyLong())).thenReturn(mockPosts);
        when(userRepositoryAdapter.findByUsername(anyString())).thenReturn(mockUser);
        when(fileUrlBuilder.build(any(FileEntity.class))).thenReturn(profilePictureUrl);
        when(userMapper.toUserDTO(any(UserEntity.class), anyList(), anyString()))
                .thenReturn(mockUserDTO);

        UserDTO actualUser = userService.getUserByUsername(username);

        assertThat(actualUser.username()).isEqualTo(username);
        assertThat(actualUser.bio()).isEqualTo(mockUser.getBio());
        assertThat(actualUser.profilePictureUrl()).isEqualTo(profilePictureUrl);
        assertThat(actualUser.posts()).hasSize(2);
        verify(userRepositoryAdapter).findByUsername(username);
        verify(postRepositoryAdapter).findByAuthorId(mockUser.getId());
        verify(userMapper).toUserDTO(mockUser, mockPosts, profilePictureUrl);
    }

    @Test
    void updateUserProfile_shouldUploadNewFileAndDeleteOld_whenProfilePictureProvided() {
        Long userId = 1L;
        Long oldFileId = 1L;
        Long newFileId = 2L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity oldFile = FileFixtures.file(oldFileId, now);
        FileEntity newFile = FileFixtures.file(newFileId, now);
        UserEntity existingUser = UserFixtures.user(userId, oldFile, now);
        String newProfilePictureUrl = "newProfilePictureUrl";
        String hashedPassword = "hashedPassword";
        UpdateUserRequestDTO request = UserFixtures.updateUserRequestDTO();
        UserEntity userToUpdate = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(hashedPassword)
                .file(newFile)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UserEntity updatedUser = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(hashedPassword)
                .file(newFile)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UpdateUserResponseDTO expectedResponse = UserFixtures.updateUserResponseDTO(
                request.username(), request.bio(), newProfilePictureUrl
        );

        when(userRepositoryAdapter.findById(userId)).thenReturn(existingUser);
        when(transactionalFileUploader.uploadWithTransactionRollback(multipartFile, "users"))
                .thenReturn(newFile);
        when(fileUrlBuilder.build(newFile)).thenReturn(newProfilePictureUrl);
        when(hasher.hash(request.password())).thenReturn(hashedPassword);
        when(userMapper.toUserEntity(request, existingUser, hashedPassword)).thenReturn(userToUpdate);
        when(userRepositoryAdapter.update(userToUpdate)).thenReturn(updatedUser);
        when(userMapper.toUpdateUserResponseDTO(updatedUser, newProfilePictureUrl))
                .thenReturn(expectedResponse);

        UpdateUserResponseDTO response = userService.updateUserProfile(userId, request, multipartFile);

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response.username()).isEqualTo(request.username());
        assertThat(response.bio()).isEqualTo(request.bio());
        assertThat(response.profilePictureUrl()).isEqualTo(newProfilePictureUrl);
        verify(userRepositoryAdapter).findById(userId);
        verify(transactionalFileUploader).uploadWithTransactionRollback(multipartFile, "users");
        verify(fileUrlBuilder).build(newFile);
        verify(hasher).hash(request.password());
        verify(userMapper).toUserEntity(request, existingUser, hashedPassword);
        verify(userRepositoryAdapter).update(userToUpdate);
        verify(userMapper).toUpdateUserResponseDTO(updatedUser, newProfilePictureUrl);
        verify(fileService).delete(oldFile);
    }

    @Test
    void updateUserProfile_shouldKeepOldFile_whenNoNewProfilePictureProvided() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity oldFile = FileFixtures.file(fileId, now);
        UserEntity existingUser = UserFixtures.user(userId, oldFile, now);
        String oldProfilePictureUrl = "oldProfilePictureUrl";
        String hashedPassword = "hashedPassword";
        UpdateUserRequestDTO request = UserFixtures.updateUserRequestDTO();
        UserEntity userToUpdate = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(hashedPassword)
                .file(oldFile)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UserEntity updatedUser = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(hashedPassword)
                .file(oldFile)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UpdateUserResponseDTO expectedResponse = UserFixtures.updateUserResponseDTO(
                request.username(), request.bio(), oldProfilePictureUrl
        );

        when(userRepositoryAdapter.findById(userId)).thenReturn(existingUser);
        when(fileUrlBuilder.build(oldFile)).thenReturn(oldProfilePictureUrl);
        when(hasher.hash(request.password())).thenReturn(hashedPassword);
        when(userMapper.toUserEntity(request, existingUser, hashedPassword)).thenReturn(userToUpdate);
        when(userRepositoryAdapter.update(userToUpdate)).thenReturn(updatedUser);
        when(userMapper.toUpdateUserResponseDTO(updatedUser, oldProfilePictureUrl))
                .thenReturn(expectedResponse);

        UpdateUserResponseDTO response = userService.updateUserProfile(userId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response.username()).isEqualTo(request.username());
        assertThat(response.bio()).isEqualTo(request.bio());
        assertThat(response.profilePictureUrl()).isEqualTo(oldProfilePictureUrl);
        verify(userRepositoryAdapter).findById(userId);
        verify(fileUrlBuilder).build(oldFile);
        verify(hasher).hash(request.password());
        verify(userMapper).toUserEntity(request, existingUser, hashedPassword);
        verify(userRepositoryAdapter).update(userToUpdate);
        verify(userMapper).toUpdateUserResponseDTO(updatedUser, oldProfilePictureUrl);
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
    }

    @Test
    void updateUserProfile_shouldNotHashPassword_whenPasswordIsNull() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity oldFile = FileFixtures.file(fileId, now);
        UserEntity existingUser = UserFixtures.user(userId, oldFile, now);
        String oldProfilePictureUrl = "oldProfilePictureUrl";
        UpdateUserRequestDTO request = new UpdateUserRequestDTO(
                "newemail@gmail.com",
                "Updated bio",
                null,
                "newusername"
        );
        UserEntity userToUpdate = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(existingUser.getPasswordHash())
                .file(oldFile)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UserEntity updatedUser = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(existingUser.getPasswordHash())
                .file(oldFile)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UpdateUserResponseDTO expectedResponse = UserFixtures.updateUserResponseDTO(
                request.username(), request.bio(), oldProfilePictureUrl
        );

        when(userRepositoryAdapter.findById(userId)).thenReturn(existingUser);
        when(fileUrlBuilder.build(oldFile)).thenReturn(oldProfilePictureUrl);
        when(userMapper.toUserEntity(request, existingUser, null)).thenReturn(userToUpdate);
        when(userRepositoryAdapter.update(userToUpdate)).thenReturn(updatedUser);
        when(userMapper.toUpdateUserResponseDTO(updatedUser, oldProfilePictureUrl))
                .thenReturn(expectedResponse);

        UpdateUserResponseDTO response = userService.updateUserProfile(userId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response.username()).isEqualTo(request.username());
        assertThat(response.bio()).isEqualTo(request.bio());
        assertThat(response.profilePictureUrl()).isEqualTo(oldProfilePictureUrl);
        verify(userRepositoryAdapter).findById(userId);
        verify(fileUrlBuilder).build(oldFile);
        verify(hasher, never()).hash(any());
        verify(userMapper).toUserEntity(request, existingUser, null);
        verify(userRepositoryAdapter).update(userToUpdate);
        verify(userMapper).toUpdateUserResponseDTO(updatedUser, oldProfilePictureUrl);
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
    }

    @Test
    void updateUserProfile_shouldHandleUserWithoutExistingProfilePicture_whenNewPictureProvided() {
        Long userId = 1L;
        Long newFileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity newFile = FileFixtures.file(newFileId, now);
        UserEntity existingUser = UserFixtures.user(userId, null, now);
        String newProfilePictureUrl = "newProfilePictureUrl";
        String hashedPassword = "hashedPassword";
        UpdateUserRequestDTO request = UserFixtures.updateUserRequestDTO();
        UserEntity userToUpdate = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(hashedPassword)
                .file(newFile)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UserEntity updatedUser = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(hashedPassword)
                .file(newFile)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UpdateUserResponseDTO expectedResponse = UserFixtures.updateUserResponseDTO(
                request.username(), request.bio(), newProfilePictureUrl
        );

        when(userRepositoryAdapter.findById(userId)).thenReturn(existingUser);
        when(transactionalFileUploader.uploadWithTransactionRollback(multipartFile, "users"))
                .thenReturn(newFile);
        when(fileUrlBuilder.build(newFile)).thenReturn(newProfilePictureUrl);
        when(hasher.hash(request.password())).thenReturn(hashedPassword);
        when(userMapper.toUserEntity(request, existingUser, hashedPassword)).thenReturn(userToUpdate);
        when(userRepositoryAdapter.update(userToUpdate)).thenReturn(updatedUser);
        when(userMapper.toUpdateUserResponseDTO(updatedUser, newProfilePictureUrl))
                .thenReturn(expectedResponse);

        UpdateUserResponseDTO response = userService.updateUserProfile(userId, request, multipartFile);

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response.username()).isEqualTo(request.username());
        assertThat(response.bio()).isEqualTo(request.bio());
        assertThat(response.profilePictureUrl()).isEqualTo(newProfilePictureUrl);
        verify(userRepositoryAdapter).findById(userId);
        verify(transactionalFileUploader).uploadWithTransactionRollback(multipartFile, "users");
        verify(fileUrlBuilder).build(newFile);
        verify(hasher).hash(request.password());
        verify(userMapper).toUserEntity(request, existingUser, hashedPassword);
        verify(userRepositoryAdapter).update(userToUpdate);
        verify(userMapper).toUpdateUserResponseDTO(updatedUser, newProfilePictureUrl);
        verify(fileService, never()).delete(any());
    }

    @Test
    void updateUserProfile_shouldReturnNullProfilePictureUrl_whenUserHasNoExistingOrNewPicture() {
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity existingUser = UserFixtures.user(userId, null, now);
        String hashedPassword = "hashedPassword";
        UpdateUserRequestDTO request = UserFixtures.updateUserRequestDTO();
        UserEntity userToUpdate = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(hashedPassword)
                .file(null)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UserEntity updatedUser = UserEntity.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .bio(request.bio())
                .passwordHash(hashedPassword)
                .file(null)
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(now)
                .build();
        UpdateUserResponseDTO expectedResponse = UserFixtures.updateUserResponseDTO(
                request.username(), request.bio(), null
        );

        when(userRepositoryAdapter.findById(userId)).thenReturn(existingUser);
        when(hasher.hash(request.password())).thenReturn(hashedPassword);
        when(userMapper.toUserEntity(request, existingUser, hashedPassword)).thenReturn(userToUpdate);
        when(userRepositoryAdapter.update(userToUpdate)).thenReturn(updatedUser);
        when(userMapper.toUpdateUserResponseDTO(updatedUser, null))
                .thenReturn(expectedResponse);

        UpdateUserResponseDTO response = userService.updateUserProfile(userId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response.username()).isEqualTo(request.username());
        assertThat(response.bio()).isEqualTo(request.bio());
        assertThat(response.profilePictureUrl()).isNull();
        verify(userRepositoryAdapter).findById(userId);
        verify(hasher).hash(request.password());
        verify(userMapper).toUserEntity(request, existingUser, hashedPassword);
        verify(userRepositoryAdapter).update(userToUpdate);
        verify(userMapper).toUpdateUserResponseDTO(updatedUser, null);
        verify(fileUrlBuilder, never()).build(any());
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
    }
}
