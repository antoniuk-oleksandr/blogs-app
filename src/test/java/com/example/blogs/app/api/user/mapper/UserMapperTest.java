package com.example.blogs.app.api.user.mapper;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDTO;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void toUserPostSummaryDto_shouldMapPostEntityToUserPostSummaryDto() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity user = UserEntity.builder().build();
        PostEntity postEntity = createPostEntity(1L, now, user);

        UserPostSummaryDTO postSummaryDto = userMapper.toUserPostSummaryDto(postEntity);

        assertThat(postSummaryDto.title()).isEqualTo(postEntity.getTitle());
        assertThat(postSummaryDto.description()).isEqualTo(postEntity.getDescription());
        assertThat(postSummaryDto.slug()).isEqualTo(postEntity.getSlug());
        assertThat(postSummaryDto.createdAt()).isEqualTo(postEntity.getCreatedAt());
        assertThat(postSummaryDto.previewImageUrl()).isEqualTo(postEntity.getPreviewImageUrl());
    }

    @Test
    void toUserPostSummaryDto_shouldHandleNullPostEntity() {
        UserPostSummaryDTO postSummaryDto = userMapper.toUserPostSummaryDto(null);

        assertThat(postSummaryDto).isNull();
    }

    @Test
    void toUserPostSummaryDto_shouldHandleNullFields() {
        PostEntity postEntity = PostEntity.builder()
                .title(null)
                .description(null)
                .slug(null)
                .previewImageUrl(null)
                .createdAt(null)
                .build();

        UserPostSummaryDTO postSummaryDto = userMapper.toUserPostSummaryDto(postEntity);

        assertThat(postSummaryDto.title()).isNull();
        assertThat(postSummaryDto.description()).isNull();
        assertThat(postSummaryDto.slug()).isNull();
        assertThat(postSummaryDto.previewImageUrl()).isNull();
        assertThat(postSummaryDto.createdAt()).isNull();
    }

    @Test
    void toUserDTO_shouldMapUserEntityAndPostsToUserDTO() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity userEntity = UserEntity.builder()
                .username("testuser")
                .bio("This is a bio")
                .profilePictureUrl("http://example.com/profile.jpg")
                .build();
        List<PostEntity> posts = List.of(
                createPostEntity(1L, now, userEntity),
                createPostEntity(2L, now, userEntity)
        );

        UserDTO userDTO = userMapper.toUserDTO(userEntity, posts);

        assertThat(userDTO.username()).isEqualTo(userEntity.getUsername());
        assertThat(userDTO.bio()).isEqualTo(userEntity.getBio());
        assertThat(userDTO.profilePictureUrl()).isEqualTo(userEntity.getProfilePictureUrl());
        assertThat(userDTO.posts()).hasSize(2);
    }

    @Test
    void toUserDTO_shouldHandleNullUserEntityAndPosts() {
        UserDTO userDTO = userMapper.toUserDTO(null, null);

        assertThat(userDTO).isNull();
    }

    @Test
    void toUserDTO_shouldHandleNullPostsList() {
        UserEntity userEntity = UserEntity.builder()
                .username("testuser")
                .bio("This is a bio")
                .profilePictureUrl("http://example.com/profile.jpg")
                .build();

        UserDTO userDTO = userMapper.toUserDTO(userEntity, null);

        assertThat(userDTO.username()).isEqualTo(userEntity.getUsername());
        assertThat(userDTO.bio()).isEqualTo(userEntity.getBio());
        assertThat(userDTO.profilePictureUrl()).isEqualTo(userEntity.getProfilePictureUrl());
        assertThat(userDTO.posts()).isNull();
    }


    PostEntity createPostEntity(Long id, LocalDateTime time, UserEntity author) {
        return PostEntity.builder()
                .id(id)
                .title("title")
                .description("description")
                .content("content")
                .slug("slug")
                .previewImageUrl("previewImageUrl")
                .createdAt(time)
                .updatedAt(time)
                .author(author)
                .build();
    }
}
