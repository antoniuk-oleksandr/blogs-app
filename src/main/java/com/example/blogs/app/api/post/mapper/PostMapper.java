package com.example.blogs.app.api.post.mapper;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper for converting between post entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    /**
     * Converts a post entity and its comments to a post DTO.
     *
     * @param post            the post entity
     * @param comments        the list of comments associated with the post
     * @param previewImageUrl the URL of the preview image
     * @return post DTO with comment summaries
     */
    PostDTO toPostDTO(PostEntity post, List<CommentEntity> comments, String previewImageUrl);

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
     * Converts a post creation request to a post entity with generated slug, file, and author.
     *
     * @param requestDTO the post creation request containing title, description, and content
     * @param slug       the generated unique slug for the post
     * @param file       the uploaded file entity for the preview image
     * @param author     the author entity creating the post
     * @return post entity ready for persistence
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "file", source = "file")
    @Mapping(target = "author", source = "author")
    PostEntity toPostEntity(PostCreateRequestDTO requestDTO, String slug, FileEntity file, UserEntity author);

    /**
     * Converts a post entity to a post update response DTO.
     *
     * @param postEntity the post entity
     * @return post update response DTO
     */
    PostUpdateResponseDTO toPostUpdateResponseDTO(PostEntity postEntity, String previewImageUrl);

    /**
     * Converts a post entity to a post creation response DTO with preview image URL.
     *
     * @param postEntity      the created post entity
     * @param previewImageUrl the generated URL for the preview image
     * @return post creation response DTO with all post details
     */
    @Mapping(target = "previewImageUrl", source = "previewImageUrl")
    PostCreateResponseDTO toPostCreateResponseDTO(PostEntity postEntity, String previewImageUrl);
}
