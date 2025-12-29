package com.example.blogs.app.api.user.repository.adapter;

import com.example.blogs.app.api.user.dto.CreateUserCommand;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.exception.*;
import com.example.blogs.app.api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserRepositoryAdapterImpl implements UserRepositoryAdapter {

    private final UserRepository userRepository;

    @Override
    public UserEntity save(CreateUserCommand command) {
        UserEntity user = UserEntity.builder()
                .username(command.username())
                .passwordHash(command.passwordHash())
                .email(command.email())
                .build();

        try {
            return userRepository.save(user);
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
    }
}
