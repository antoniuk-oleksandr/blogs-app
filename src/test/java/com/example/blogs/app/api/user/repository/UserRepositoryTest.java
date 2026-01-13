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
                .email("test@gmail.com")
                .build();

        UserEntity actualUser = userRepository.save(partialUser);
        LocalDateTime now = LocalDateTime.now();

        assertUserEntity(actualUser, now);
    }

    @Test
    void findUserByUsernameOrEmail_shouldReturnUserByUsername_whenUserExists() {
        UserEntity partialUser = createTestUserEntity();
        userRepository.save(partialUser);
        LocalDateTime now = LocalDateTime.now();

        Optional<UserEntity> actualUser = userRepository
                .findUserByUsernameOrEmail("test", "test");

        assertOptionalUserEntity(actualUser, now);
    }

    @Test
    void findUserByUsernameOrEmail_shouldReturnUserByEmail_whenUserExists() {
        UserEntity partialUser = createTestUserEntity();
        userRepository.save(partialUser);
        LocalDateTime now = LocalDateTime.now();

        Optional<UserEntity> actualUser = userRepository
                .findUserByUsernameOrEmail("test@gmail.com", "test@gmail.com");

        assertOptionalUserEntity(actualUser, now);
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        UserEntity partialUser = createTestUserEntity();
        userRepository.save(partialUser);
        LocalDateTime now = LocalDateTime.now();

        Optional<UserEntity> actualUser = userRepository
                .findByUsername("test");

        assertOptionalUserEntity(actualUser, now);
    }

    private UserEntity createTestUserEntity() {
        return UserEntity.builder()
                .username("test")
                .passwordHash("passwordHash")
                .email("test@gmail.com")
                .build();
    }

    private void assertUserEntity(UserEntity user, LocalDateTime time) {
        assertThat(user.getUsername()).isEqualTo("test");
        assertThat(user.getEmail()).isEqualTo("test@gmail.com");
        assertThat(user.getPasswordHash()).isEqualTo("passwordHash");
        assertThat(user.getId()).isNotNull().isNotNegative();
        assertThat(user.getUpdatedAt())
                .isCloseTo(time, within(1, ChronoUnit.SECONDS));
        assertThat(user.getCreatedAt())
                .isCloseTo(time, within(1, ChronoUnit.SECONDS));
    }

    private void assertOptionalUserEntity(Optional<UserEntity> user, LocalDateTime time) {
        assertThat(user).isPresent();
        assertUserEntity(user.get(), time);
    }
}
