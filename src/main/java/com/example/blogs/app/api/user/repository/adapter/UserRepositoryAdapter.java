package com.example.blogs.app.api.user.repository.adapter;

import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;

public interface UserRepositoryAdapter {
    UserEntity save(CreateUserCommand command);
}
