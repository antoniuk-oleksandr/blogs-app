package com.example.blogs.app.api.user.repository;

import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
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
        UserEntity user = UserFixtures.user(null);

        UserEntity savedUser = userRepository.save(user);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        assertUserEntity(savedUser, now);
    }

    @Test
    void findUserByUsernameOrEmail_shouldReturnUserByUsername_whenUserExists() {
        UserEntity user = UserFixtures.user(null);
        userRepository.save(user);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Optional<UserEntity> actualUser = userRepository
                .findUserByUsernameOrEmail(user.getUsername(), user.getUsername());

        assertOptionalUserEntity(actualUser, now);
    }

    @Test
    void findUserByUsernameOrEmail_shouldReturnUserByEmail_whenUserExists() {
        UserEntity user = UserFixtures.user(null);
        userRepository.save(user);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Optional<UserEntity> actualUser = userRepository
                .findUserByUsernameOrEmail(user.getEmail(), user.getEmail());

        assertOptionalUserEntity(actualUser, now);
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        UserEntity user = UserFixtures.user(null);
        userRepository.save(user);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Optional<UserEntity> actualUser = userRepository
                .findByUsername(user.getUsername());

        assertOptionalUserEntity(actualUser, now);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        UserEntity user = UserFixtures.user(null);
        UserEntity savedUser = userRepository.save(user);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Optional<UserEntity> actualUser = userRepository
                .findById(savedUser.getId());

        assertOptionalUserEntity(actualUser, now);
    }

    private void assertUserEntity(UserEntity user, LocalDateTime time) {
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getEmail()).isEqualTo("email@gmail.com");
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
