package com.example.blogs.app.api.user.repository;

import com.example.blogs.app.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access for user entities with unique constraints on username and email.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * Finds a user by username or email address.
     *
     * @param username username to search for
     * @param email    email address to search for
     * @return optional containing user if found, empty otherwise
     */
    Optional<UserEntity> findUserByUsernameOrEmail(String username, String email);

    /**
     * Finds a user by username.
     *
     * @param username username to search for
     * @return optional containing user if found, empty otherwise
     */
    Optional<UserEntity> findByUsername(String username);
}
