package com.example.blogs.app.api.user.service;

import com.example.blogs.app.api.user.exception.EmailTakenException;
import com.example.blogs.app.api.user.exception.FailedToCreateUser;
import com.example.blogs.app.api.user.exception.UsernameTakenException;
import com.example.blogs.app.api.user.repository.UserRepository;
import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

/**
 * Translates database constraint violations into domain-specific exceptions during user creation.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity createUser(CreateUserCommand command) {
        UserEntity user = UserEntity.builder()
                .username(command.username())
                .passwordHash(command.passwordHash())
                .email(command.email())
                .build();

        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            Throwable cause = e.getCause();

            if (cause instanceof ConstraintViolationException cve) {
                String constraint = cve.getConstraintName();

                if ("users_username_key".equals(constraint)) {
                    throw new UsernameTakenException();
                }
                if ("users_email_key".equals(constraint)) {
                    throw new EmailTakenException();
                }
            }

            throw new FailedToCreateUser();
        }

        return user;
    }
}
