package com.example.blogs.app.api.user.repository;

import com.example.blogs.app.api.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Testcontainers
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18.0-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

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
}
