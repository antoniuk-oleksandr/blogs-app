package com.example.blogs.app.api.user.mapper;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.user.dto.UserPostSummaryDto;
import com.example.blogs.app.api.user.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(UserEntity userEntity, List<PostEntity> posts);

    UserPostSummaryDto toUserPostSummaryDto(PostEntity postEntity);
}
