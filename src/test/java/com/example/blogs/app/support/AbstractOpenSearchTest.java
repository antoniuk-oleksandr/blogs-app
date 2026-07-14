package com.example.blogs.app.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base test class for integration tests requiring an OpenSearch instance.
 * Provides shared OpenSearch container configuration and automatic property registration
 * for Spring Boot tests. Extend this class to enable OpenSearch-backed integration tests.
 */
@Testcontainers
public abstract class AbstractOpenSearchTest {

    /**
     * Registers OpenSearch container connection properties dynamically for Spring context.
     * Configures host, port, and credentials for the test OpenSearch instance.
     *
     * @param registry Spring's dynamic property registry for test configuration
     */
    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        var container = SharedOpenSearchContainer.getInstance();
        registry.add("opensearch.host", () -> "http://localhost");
        registry.add("opensearch.port", () -> container.getMappedPort(9200));
        registry.add("opensearch.username", () -> "");
        registry.add("opensearch.password", () -> "");
    }
}
