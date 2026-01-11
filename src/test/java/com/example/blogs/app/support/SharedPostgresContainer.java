package com.example.blogs.app.support;

import lombok.Getter;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Singleton PostgreSQL container shared across all integration tests.
 * Uses Testcontainers to provide an isolated PostgreSQL 18.0 instance for testing.
 * The container is started once during class loading and reused across all test classes
 * to improve test execution performance.
 */
public class SharedPostgresContainer {
    /**
     * Singleton PostgreSQL container instance configured with test credentials.
     * Container lifecycle is managed automatically by the static initializer block.
     */
    @Getter
    private static final PostgreSQLContainer<?> instance =
            new PostgreSQLContainer<>("postgres:18.0-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    static {
        instance.start();
    }
}
