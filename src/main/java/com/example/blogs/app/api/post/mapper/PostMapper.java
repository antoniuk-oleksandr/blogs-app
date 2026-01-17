package com.example.blogs.app.api.post.mapper;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapper for converting between post entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    /**
     * Converts a post entity and its comments to a post DTO.
     *
     * @param post     the post entity
     * @param comments the list of comments associated with the post
     * @return post DTO with comment summaries
     */
    PostDTO toPostDTO(PostEntity post, List<CommentEntity> comments);

    /**
     * Converts a user entity to a post user summary DTO.
     *
     * @param post the user entity
     * @return post user summary DTO
     */
    PostUserSummaryDTO toPostUserSummaryDTO(UserEntity post);

    /**
     * Converts a comment entity to a post comment summary DTO.
     *
     * @param comment the comment entity
     * @return post comment summary DTO
     */
    PostCommentSummaryDTO toPostCommentSummaryDTO(CommentEntity comment);

    /**
     * Updates a post entity with non-null fields from the request DTO.
     * Null values in the request are ignored, preserving existing entity values.
     *
     * @param requestDTO the update request containing fields to update
     * @param postEntity the existing post entity to update
     * @return updated post entity with merged values
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostEntity toPostEntity(PostUpdateRequestDTO requestDTO, @MappingTarget PostEntity postEntity);

    /**
     * Converts a post entity to a post update response DTO.
     *
     * @param postEntity the post entity
     * @return post update response DTO
     */
    PostUpdateResponseDTO toPostUpdateResponseDTO(PostEntity postEntity);
}
