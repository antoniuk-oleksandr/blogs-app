package com.example.blogs.app.api.post.mapper;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.post.dto.PostCommentSummaryDTO;
import com.example.blogs.app.api.post.dto.PostDTO;
import com.example.blogs.app.api.post.dto.PostUserSummaryDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDTO toPostDTO(PostEntity post, List<CommentEntity> comments);

    PostUserSummaryDTO toPostUserSummaryDTO(UserEntity post);

    PostCommentSummaryDTO toPostCommentSummaryDTO(CommentEntity comment);
}
