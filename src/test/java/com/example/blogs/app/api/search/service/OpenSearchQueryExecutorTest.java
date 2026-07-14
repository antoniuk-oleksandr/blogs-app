package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.search.dto.OpenSearchPostHit;
import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.dto.SearchPostAuthor;
import com.example.blogs.app.api.search.exception.FailedToSearchPostsException;
import com.example.blogs.app.api.search.fixture.SearchFixtures;
import com.example.blogs.app.support.SharedOpenSearchContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.HttpHost;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5Transport;
import org.opensearch.client.transport.httpclient5.internal.Node;
import org.opensearch.client.transport.httpclient5.internal.NodeSelector;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
class OpenSearchQueryExecutorTest {

    private static OpenSearchQueryExecutor executor;

    private static OpenSearchClient client;

    @BeforeAll
    static void setUpAll() throws Exception {
        var container = SharedOpenSearchContainer.getInstance();
        int port = container.getMappedPort(9200);

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        httpClient.start();

        client = new OpenSearchClient(
                new ApacheHttpClient5Transport(
                        httpClient,
                        new org.apache.hc.core5.http.Header[0],
                        List.of(new Node(new HttpHost("localhost", port))),
                        new JacksonJsonpMapper(objectMapper),
                        null,
                        "",
                        new ApacheHttpClient5Transport.FailureListener(),
                        NodeSelector.ANY,
                        false,
                        false,
                        false
                )
        );

        executor = new OpenSearchQueryExecutor(client);
        createIndex();
        indexTestDocuments();
        client.indices().refresh(r -> r.index("posts"));
    }

    static void createIndex() {
        try {
            client.indices().create(r -> r.index("posts"));
        } catch (Exception e) {
            // Index may already exist from a previous run, ignore
        }
    }

    static void indexTestDocuments() throws Exception {
        SearchPostAuthor author = SearchFixtures.searchPostAuthor(1L);
        LocalDateTime now = LocalDateTime.now().withNano(0);

        SearchPost post1 = new SearchPost(
                1L, "Spring Boot Guide", "A guide to Spring Boot",
                "Spring Boot content", "spring-boot-guide", now, 10, 5, "url1", author
        );
        SearchPost post2 = new SearchPost(
                2L, "Java Programming", "A guide to Java programming",
                "Java content", "java-programming", now.minusDays(1), 5, 2, "url2", author
        );

        client.index(r -> r.index("posts").id("1").document(post1));
        client.index(r -> r.index("posts").id("2").document(post2));
    }

    @Test
    void execute_shouldReturnAllResults_whenMatchAllQueryIsUsed() {
        SearchRequest request = SearchRequest.of(r -> r
                .index("posts")
                .query(q -> q.matchAll(m -> m))
        );

        List<OpenSearchPostHit> results = executor.execute(request);

        assertThat(results)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void execute_shouldReturnMatchingResults_whenQueryTermMatchesTitle() {
        SearchRequest request = SearchRequest.of(r -> r
                .index("posts")
                .query(q -> q.match(m -> m
                        .field("title")
                        .query(v -> v.stringValue("Spring"))
                ))
        );

        List<OpenSearchPostHit> results = executor.execute(request);

        assertThat(results).isNotNull().isNotEmpty();
        assertThat(results).extracting(result -> result.post().title()).contains("Spring Boot Guide");
    }

    @Test
    void execute_shouldReturnEmptyList_whenNoDocumentsMatchQuery() {
        SearchRequest request = SearchRequest.of(r -> r
                .index("posts")
                .query(q -> q.match(m -> m
                        .field("title")
                        .query(v -> v.stringValue("zzz_completely_nonexistent_xyz_987654"))
                ))
        );

        List<OpenSearchPostHit> results = executor.execute(request);

        assertThat(results).isEmpty();
    }

    @Test
    void execute_shouldThrowFaildToSearchPostsException_whenIndexDoesNotExist() {
        SearchRequest request = SearchRequest.of(r -> r
                .index("nonexistent-index-xyz-123")
                .query(q -> q.matchAll(m -> m))
        );

        assertThatThrownBy(() -> executor.execute(request))
                .isInstanceOf(FailedToSearchPostsException.class)
                .hasMessage("Failed to search posts");
    }
}
