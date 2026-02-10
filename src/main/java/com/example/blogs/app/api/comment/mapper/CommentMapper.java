package com.example.blogs.app.api.comment.mapper;

import com.example.blogs.app.api.comment.dto.CommentDTO;
import com.example.blogs.app.api.comment.entity.CommentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDTO toCommentDTO(CommentEntity commentEntity, Long postId, Long authorId);
}
