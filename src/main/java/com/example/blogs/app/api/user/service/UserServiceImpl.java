package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.repository.adapter.UserRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Translates database constraint violations into domain-specific exceptions during user creation.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositoryAdapter userRepositoryAdapter;

    @Override
    public UserEntity createUser(CreateUserCommand command) {
        return userRepositoryAdapter.save(command);
    }
}
