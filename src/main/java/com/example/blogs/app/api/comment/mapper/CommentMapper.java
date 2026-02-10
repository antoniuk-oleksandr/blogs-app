package com.example.blogs.app.api.comment.mapper;

import com.example.blogs.app.api.comment.dto.CommentDTO;
import com.example.blogs.app.api.comment.entity.CommentEntity;
import org.mapstruct.Mapper;

/**
 * Maps between comment entities and DTOs using MapStruct.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Converts a comment entity to a comment DTO.
     *
     * @param commentEntity the comment entity to convert
     * @param postId the ID of the post the comment belongs to
     * @param authorId the ID of the comment author
     * @return comment DTO with all fields populated
     */
    CommentDTO toCommentDTO(CommentEntity commentEntity, Long postId, Long authorId);
}
