package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.service.PostService;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.dto.UserPostSummaryDto;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.mapper.UserMapper;
import com.example.blogs.app.api.user.repository.adapter.UserRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Translates database constraint violations into domain-specific exceptions during user creation.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryAdapter userRepositoryAdapter;

    private final PostService postService;

    private final UserMapper userMapper;

    @Override
    public UserEntity createUser(CreateUserCommand command) {
        return userRepositoryAdapter.save(command);
    }

    @Override
    public UserEntity findUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepositoryAdapter.findByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        UserEntity userEntity = userRepositoryAdapter.findByUsername(username);
        List<PostEntity> postEntities = postService.getPostsByUserId(userEntity.getId());

        return userMapper.toUserDTO(userEntity, postEntities);
    }
}
