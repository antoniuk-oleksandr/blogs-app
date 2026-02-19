package com.example.blogs.app.api.user.mapper;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.dto.UpdateUserRequestDTO;
import com.example.blogs.app.api.user.dto.UpdateUserResponseDTO;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDTO;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for converting user entities and posts to data transfer objects.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps user entity and associated posts to a complete user DTO.
     *
     * @param userEntity user entity with profile information
     * @param posts      list of post entities authored by the user
     * @param profilePictureUrl URL of the user's profile picture to include in the post summary
     * @return user DTO with profile and post summaries
     */
    UserDTO toUserDTO(UserEntity userEntity, List<PostEntity> posts, String profilePictureUrl);

    /**
     * Maps a post entity to a post summary DTO.
     *
     * @param postEntity        post entity to map
     * @param profilePictureUrl URL of the user's profile picture to include in the post summary
     * @return post summary DTO
     */
    UserPostSummaryDTO toUserPostSummaryDto(PostEntity postEntity, String profilePictureUrl);

    /**
     * Creates a user entity with only the ID populated.
     *
     * @param id the user ID
     * @return user entity with only ID set
     */
    UserEntity toUserEntity(Long id);

    /**
     * Updates user entity with values from request DTO and password hash.
     * Uses IGNORE strategy - only non-null fields from requestDTO are applied.
     *
     * @param requestDTO  DTO containing fields to update (null fields are ignored)
     * @param userEntity  existing user entity to update (modified in place)
     * @param passwordHash optional new password hash (null if password not changed)
     * @return the modified user entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserEntity toUserEntity(
            UpdateUserRequestDTO requestDTO,
            @MappingTarget UserEntity userEntity,
            String passwordHash
    );

    /**
     * Maps user entity and profile picture URL to update response DTO.
     *
     * @param userEntity        updated user entity
     * @param profilePictureUrl URL of the user's profile picture
     * @return update response DTO with username, bio, and profile picture URL
     */
    UpdateUserResponseDTO toUpdateUserResponseDTO(UserEntity userEntity, String profilePictureUrl);
}
