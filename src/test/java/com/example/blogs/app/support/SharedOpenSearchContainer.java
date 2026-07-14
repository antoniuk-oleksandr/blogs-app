package com.example.blogs.app.support;

import lombok.Getter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Singleton OpenSearch container shared across all integration tests.
 * Uses Testcontainers to provide an isolated OpenSearch instance for testing.
 * The container is started once during class loading and reused across all test classes
 * to improve test execution performance. Security plugin is disabled for simplified test setup.
 */
public class SharedOpenSearchContainer {

    /**
     * Singleton OpenSearch container instance configured with test credentials.
     * Container lifecycle is managed automatically by the static initializer block.
     */
    @Getter
    private static final GenericContainer<?> instance =
            new GenericContainer<>("opensearchproject/opensearch:3.5.0")
                    .withEnv("discovery.type", "single-node")
                    .withEnv("DISABLE_SECURITY_PLUGIN", "true")
                    .withEnv("OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m")
                    .withExposedPorts(9200)
                    .waitingFor(Wait.forHttp("/").forPort(9200).forStatusCode(200));

    static {
        instance.start();
    }
}
