package com.example.blogs.app.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base test class for integration tests requiring PostgreSQL database.
 * Provides shared PostgreSQL container configuration and automatic property registration
 * for Spring Boot tests. Extend this class to enable database-backed integration tests.
 */
@Testcontainers
public abstract class AbstractPostgresTest {

    /**
     * Registers PostgreSQL container connection properties dynamically for Spring context.
     * Configures datasource URL, credentials, and Hibernate validation mode.
     *
     * @param registry Spring's dynamic property registry for test configuration
     */
    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        var postgres = SharedPostgresContainer.getInstance();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }
}
