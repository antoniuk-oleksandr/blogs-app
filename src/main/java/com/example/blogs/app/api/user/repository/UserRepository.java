package com.example.blogs.app.api.user.repository;

import com.example.blogs.app.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for user entities with unique constraints on username and email.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
