package com.example.blogs.app.api.user.mapper;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.user.dto.UpdateUserRequestDTO;
import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDTO;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void toUserPostSummaryDto_shouldMapPostEntityToUserPostSummaryDto() {
        Long fileId = 1L;
        Long userId = 1L;
        Long postId = 1L;
        String profilePictureUrl = "profilePictureUrl";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, user);

        UserPostSummaryDTO postSummaryDto = userMapper.toUserPostSummaryDto(post, profilePictureUrl);

        assertThat(postSummaryDto.title()).isEqualTo(post.getTitle());
        assertThat(postSummaryDto.description()).isEqualTo(post.getDescription());
        assertThat(postSummaryDto.slug()).isEqualTo(post.getSlug());
        assertThat(postSummaryDto.createdAt()).isEqualTo(post.getCreatedAt());
    }

    @Test
    void toUserPostSummaryDto_shouldHandleNullPostEntity() {
        UserPostSummaryDTO postSummaryDto = userMapper.toUserPostSummaryDto(null, null);

        assertThat(postSummaryDto).isNull();
    }

    @Test
    void toUserPostSummaryDto_shouldHandleNullFields() {
        PostEntity postEntity = PostEntity.builder()
                .title(null)
                .description(null)
                .slug(null)
                .createdAt(null)
                .build();

        UserPostSummaryDTO postSummaryDto = userMapper.toUserPostSummaryDto(postEntity, null);

        assertThat(postSummaryDto.title()).isNull();
        assertThat(postSummaryDto.description()).isNull();
        assertThat(postSummaryDto.slug()).isNull();
        assertThat(postSummaryDto.previewImageUrl()).isNull();
        assertThat(postSummaryDto.createdAt()).isNull();
        assertThat(postSummaryDto.previewImageUrl()).isNull();
    }

    @Test
    void toUserDTO_shouldMapUserEntityAndPostsToUserDTO() {
        Long fileId = 1L;
        Long userId = 1L;
        Long firstPostId = 1L;
        Long secondPostId = 2L;
        String profilePictureUrl = "profilePictureUrl";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);
        List<PostEntity> posts = List.of(
                PostFixtures.post(firstPostId, now, user),
                PostFixtures.post(secondPostId, now, user)
        );

        UserDTO userDTO = userMapper.toUserDTO(user, posts, profilePictureUrl);

        assertThat(userDTO.username()).isEqualTo(user.getUsername());
        assertThat(userDTO.bio()).isEqualTo(user.getBio());
        assertThat(userDTO.profilePictureUrl()).isEqualTo(profilePictureUrl);
        assertThat(userDTO.posts()).hasSize(2);
    }

    @Test
    void toUserDTO_shouldHandleNullUserEntityAndPosts() {
        UserDTO userDTO = userMapper.toUserDTO(null, null, null);

        assertThat(userDTO).isNull();
    }

    @Test
    void toUserDTO_shouldHandleNullPostsList() {
        Long fileId = 1L;
        Long userId = 1L;
        String profilePictureUrl = "profilePictureUrl";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);

        UserDTO userDTO = userMapper.toUserDTO(user, null, profilePictureUrl);

        assertThat(userDTO.username()).isEqualTo(user.getUsername());
        assertThat(userDTO.bio()).isEqualTo(user.getBio());
        assertThat(userDTO.profilePictureUrl()).isEqualTo(profilePictureUrl);
        assertThat(userDTO.posts()).isNull();
    }

    @Test
    void toUserEntity_shouldMapIdToUserEntity() {
        Long userId = 42L;

        UserEntity userEntity = userMapper.toUserEntity(userId);

        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getId()).isEqualTo(userId);
    }

    @Test
    void toUserEntity_shouldReturnNull_whenIdIsNull() {
        UserEntity userEntity = userMapper.toUserEntity(null);

        assertThat(userEntity).isNull();
    }

    @Test
    void toUserEntity_shouldUpdateUserEntity_whenAllFieldsProvided() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity existingUser = UserFixtures.user(userId, file, now);
        String newPasswordHash = "newPasswordHash";
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO(
                "newemail@gmail.com",
                "New bio",
                "newPassword",
                "newusername"
        );

        UserEntity updatedUser = userMapper.toUserEntity(requestDTO, existingUser, newPasswordHash);

        assertThat(updatedUser).isSameAs(existingUser);
        assertThat(updatedUser.getUsername()).isEqualTo("newusername");
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@gmail.com");
        assertThat(updatedUser.getBio()).isEqualTo("New bio");
        assertThat(updatedUser.getPasswordHash()).isEqualTo(newPasswordHash);
    }

    @Test
    void toUserEntity_shouldUpdateOnlyProvidedFields_whenSomeFieldsAreNull() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity existingUser = UserFixtures.user(userId, file, now);
        String originalUsername = existingUser.getUsername();
        String originalPasswordHash = existingUser.getPasswordHash();
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO(
                "newemail@gmail.com",
                "New bio",
                null,
                null
        );

        UserEntity updatedUser = userMapper.toUserEntity(requestDTO, existingUser, null);

        assertThat(updatedUser).isSameAs(existingUser);
        assertThat(updatedUser.getUsername()).isEqualTo(originalUsername);
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@gmail.com");
        assertThat(updatedUser.getBio()).isEqualTo("New bio");
        assertThat(updatedUser.getPasswordHash()).isEqualTo(originalPasswordHash);
    }

    @Test
    void toUserEntity_shouldNotUpdateFields_whenRequestDtoIsNull() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity existingUser = UserFixtures.user(userId, file, now);
        String originalUsername = existingUser.getUsername();
        String originalEmail = existingUser.getEmail();
        String originalBio = existingUser.getBio();
        String originalPasswordHash = existingUser.getPasswordHash();

        UserEntity updatedUser = userMapper.toUserEntity(null, existingUser, null);

        assertThat(updatedUser).isSameAs(existingUser);
        assertThat(updatedUser.getUsername()).isEqualTo(originalUsername);
        assertThat(updatedUser.getEmail()).isEqualTo(originalEmail);
        assertThat(updatedUser.getBio()).isEqualTo(originalBio);
        assertThat(updatedUser.getPasswordHash()).isEqualTo(originalPasswordHash);
    }

    @Test
    void toUserEntity_shouldUpdateOnlyPasswordHash_whenOnlyPasswordHashProvided() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity existingUser = UserFixtures.user(userId, file, now);
        String originalUsername = existingUser.getUsername();
        String originalEmail = existingUser.getEmail();
        String originalBio = existingUser.getBio();
        String newPasswordHash = "newPasswordHash";

        UserEntity updatedUser = userMapper.toUserEntity(null, existingUser, newPasswordHash);

        assertThat(updatedUser).isSameAs(existingUser);
        assertThat(updatedUser.getUsername()).isEqualTo(originalUsername);
        assertThat(updatedUser.getEmail()).isEqualTo(originalEmail);
        assertThat(updatedUser.getBio()).isEqualTo(originalBio);
        assertThat(updatedUser.getPasswordHash()).isEqualTo(newPasswordHash);
    }

    @Test
    void toUpdateUserResponseDTO_shouldMapUserEntityToUpdateUserResponseDTO() {
        Long userId = 1L;
        Long fileId = 1L;
        String profilePictureUrl = "profilePictureUrl";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);

        UpdateUserResponseDTO responseDTO = userMapper.toUpdateUserResponseDTO(user, profilePictureUrl);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.username()).isEqualTo(user.getUsername());
        assertThat(responseDTO.bio()).isEqualTo(user.getBio());
        assertThat(responseDTO.profilePictureUrl()).isEqualTo(profilePictureUrl);
    }

    @Test
    void toUpdateUserResponseDTO_shouldReturnNull_whenUserEntityAndProfilePictureUrlAreNull() {
        UpdateUserResponseDTO responseDTO = userMapper.toUpdateUserResponseDTO(null, null);

        assertThat(responseDTO).isNull();
    }

    @Test
    void toUpdateUserResponseDTO_shouldMapWithNullProfilePictureUrl() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);

        UpdateUserResponseDTO responseDTO = userMapper.toUpdateUserResponseDTO(user, null);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.username()).isEqualTo(user.getUsername());
        assertThat(responseDTO.bio()).isEqualTo(user.getBio());
        assertThat(responseDTO.profilePictureUrl()).isNull();
    }

    @Test
    void toUpdateUserResponseDTO_shouldMapWithNullUserEntityFields() {
        UserEntity user = UserEntity.builder()
                .username(null)
                .bio(null)
                .build();
        String profilePictureUrl = "profilePictureUrl";

        UpdateUserResponseDTO responseDTO = userMapper.toUpdateUserResponseDTO(user, profilePictureUrl);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.username()).isNull();
        assertThat(responseDTO.bio()).isNull();
        assertThat(responseDTO.profilePictureUrl()).isEqualTo(profilePictureUrl);
    }

    @Test
    void toUserDTO_shouldMapWithOnlyUserEntity() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);

        UserDTO userDTO = userMapper.toUserDTO(user, null, null);

        assertThat(userDTO).isNotNull();
        assertThat(userDTO.username()).isEqualTo(user.getUsername());
        assertThat(userDTO.bio()).isEqualTo(user.getBio());
        assertThat(userDTO.profilePictureUrl()).isNull();
        assertThat(userDTO.posts()).isNull();
    }

    @Test
    void toUserDTO_shouldMapWithOnlyPosts() {
        Long userId = 1L;
        Long fileId = 1L;
        Long postId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);
        List<PostEntity> posts = List.of(PostFixtures.post(postId, now, user));

        UserDTO userDTO = userMapper.toUserDTO(null, posts, null);

        assertThat(userDTO).isNotNull();
        assertThat(userDTO.username()).isNull();
        assertThat(userDTO.bio()).isNull();
        assertThat(userDTO.profilePictureUrl()).isNull();
        assertThat(userDTO.posts()).hasSize(1);
    }

    @Test
    void toUpdateUserResponseDTO_shouldMapWithOnlyUserEntity() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);

        UpdateUserResponseDTO responseDTO = userMapper.toUpdateUserResponseDTO(user, null);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.username()).isEqualTo(user.getUsername());
        assertThat(responseDTO.bio()).isEqualTo(user.getBio());
        assertThat(responseDTO.profilePictureUrl()).isNull();
    }

    @Test
    void toUpdateUserResponseDTO_shouldMapWithOnlyProfilePictureUrl() {
        String profilePictureUrl = "profilePictureUrl";

        UpdateUserResponseDTO responseDTO = userMapper.toUpdateUserResponseDTO(null, profilePictureUrl);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.username()).isNull();
        assertThat(responseDTO.bio()).isNull();
        assertThat(responseDTO.profilePictureUrl()).isEqualTo(profilePictureUrl);
    }

    @Test
    void toUserPostSummaryDto_shouldMapWithOnlyPostEntity() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity user = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, user);

        UserPostSummaryDTO postSummaryDto = userMapper.toUserPostSummaryDto(post, null);

        assertThat(postSummaryDto).isNotNull();
        assertThat(postSummaryDto.title()).isEqualTo(post.getTitle());
        assertThat(postSummaryDto.description()).isEqualTo(post.getDescription());
        assertThat(postSummaryDto.slug()).isEqualTo(post.getSlug());
        assertThat(postSummaryDto.createdAt()).isEqualTo(post.getCreatedAt());
        assertThat(postSummaryDto.previewImageUrl()).isNull();
    }

    @Test
    void toUserEntity_shouldUpdateOnlyEmail_whenOnlyEmailProvided() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity existingUser = UserFixtures.user(userId, file, now);
        String originalUsername = existingUser.getUsername();
        String originalBio = existingUser.getBio();
        String originalPasswordHash = existingUser.getPasswordHash();
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO(
                "newemail@gmail.com",
                null,
                null,
                null
        );

        UserEntity updatedUser = userMapper.toUserEntity(requestDTO, existingUser, null);

        assertThat(updatedUser).isSameAs(existingUser);
        assertThat(updatedUser.getUsername()).isEqualTo(originalUsername);
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@gmail.com");
        assertThat(updatedUser.getBio()).isEqualTo(originalBio);
        assertThat(updatedUser.getPasswordHash()).isEqualTo(originalPasswordHash);
    }

    @Test
    void toUserEntity_shouldUpdateOnlyUsername_whenOnlyUsernameProvided() {
        Long userId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity existingUser = UserFixtures.user(userId, file, now);
        String originalEmail = existingUser.getEmail();
        String originalBio = existingUser.getBio();
        String originalPasswordHash = existingUser.getPasswordHash();
        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO(
                null,
                null,
                null,
                "newusername"
        );

        UserEntity updatedUser = userMapper.toUserEntity(requestDTO, existingUser, null);

        assertThat(updatedUser).isSameAs(existingUser);
        assertThat(updatedUser.getUsername()).isEqualTo("newusername");
        assertThat(updatedUser.getEmail()).isEqualTo(originalEmail);
        assertThat(updatedUser.getBio()).isEqualTo(originalBio);
        assertThat(updatedUser.getPasswordHash()).isEqualTo(originalPasswordHash);
    }
}
