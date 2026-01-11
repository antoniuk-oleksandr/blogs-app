package com.example.blogs.app.support;

import lombok.Getter;
import org.testcontainers.containers.PostgreSQLContainer;

public class SharedPostgresContainer {
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
