package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.user.dto.UserDTO;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.service.PostService;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.mapper.UserMapper;
import com.example.blogs.app.api.user.repository.adapter.UserRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates user operations by coordinating repository access, post retrieval, and entity-to-DTO mapping.
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
    public UserEntity getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepositoryAdapter.findByUsernameOrEmail(usernameOrEmail);
    }

    /**
     * Retrieves complete user profile with posts by username.
     * Fetches user entity, loads associated posts, and maps to DTO.
     *
     * @param username the username to search for
     * @return user data transfer object with profile information and post summaries
     * @throws com.example.blogs.app.api.user.exception.UserNotFoundException     if no user is found
     * @throws com.example.blogs.app.api.user.exception.FailedToFindUserException for database errors
     */
    @Override
    public UserDTO getUserByUsername(String username) {
        UserEntity userEntity = userRepositoryAdapter.findByUsername(username);
        List<PostEntity> postEntities = postService.getPostsByUserId(userEntity.getId());

        return userMapper.toUserDTO(userEntity, postEntities);
    }
}
