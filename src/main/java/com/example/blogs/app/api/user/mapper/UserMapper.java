package com.example.blogs.app.api.user.mapper;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDto;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.mapstruct.Mapper;

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
     * @return user DTO with profile and post summaries
     */
    UserDTO toUserDTO(UserEntity userEntity, List<PostEntity> posts);

    /**
     * Maps a post entity to a post summary DTO.
     *
     * @param postEntity post entity to map
     * @return post summary DTO
     */
    UserPostSummaryDto toUserPostSummaryDto(PostEntity postEntity);
}
