package com.example.blogs.app.api.user.repository;

import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_shouldSaveUserSuccessfully() {
        UserEntity partialUser = UserEntity.builder()
                .username("test")
                .passwordHash("passwordHash")
                .email("email@gmail.com")
                .build();

        UserEntity actualUser = userRepository.save(partialUser);

        LocalDateTime now = LocalDateTime.now();
        assertThat(actualUser.getId()).isNotNull().isNotNegative();
        assertThat(actualUser.getUsername()).isEqualTo("test");
        assertThat(actualUser.getEmail()).isEqualTo("email@gmail.com");
        assertThat(actualUser.getBio()).isNull();
        assertThat(actualUser.getProfilePictureUrl()).isNull();
        assertThat(actualUser.getUpdatedAt())
                .isCloseTo(now, within(1, ChronoUnit.SECONDS));
        assertThat(actualUser.getCreatedAt())
                .isCloseTo(now, within(1, ChronoUnit.SECONDS));
    }

    @Test
    void findUserByUsernameOrEmail_shouldReturnUserByUsername_whenUserExists() {
        UserEntity partialUser = UserEntity.builder()
                .username("test")
                .passwordHash("passwordHash")
                .email("test@gmail.com")
                .build();

        userRepository.save(partialUser);

        Optional<UserEntity> actualUser = userRepository
                .findUserByUsernameOrEmail("test", "test");

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getUsername()).isEqualTo("test");
        assertThat(actualUser.get().getEmail()).isEqualTo("test@gmail.com");
        assertThat(actualUser.get().getPasswordHash()).isEqualTo("passwordHash");
        assertThat(actualUser.get().getId()).isNotNull().isNotNegative();
    }

    @Test
    void findUserByUsernameOrEmail_shouldReturnUserByEmail_whenUserExists() {
        UserEntity partialUser = UserEntity.builder()
                .username("test")
                .passwordHash("passwordHash")
                .email("test@gmail.com")
                .build();

        userRepository.save(partialUser);

        Optional<UserEntity> actualUser = userRepository
                .findUserByUsernameOrEmail("test@gmail.com", "test@gmail.com");

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getUsername()).isEqualTo("test");
        assertThat(actualUser.get().getEmail()).isEqualTo("test@gmail.com");
        assertThat(actualUser.get().getPasswordHash()).isEqualTo("passwordHash");
        assertThat(actualUser.get().getId()).isNotNull().isNotNegative();
    }
}
