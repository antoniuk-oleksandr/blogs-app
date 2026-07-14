package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.search.dto.OpenSearchPostHit;
import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.exception.FailedToSearchPostsException;
import lombok.AllArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class OpenSearchQueryExecutor {

    private final OpenSearchClient openSearchClient;

    public List<OpenSearchPostHit> execute(SearchRequest request) {
        try {
            SearchResponse<SearchPost> response = openSearchClient.search(request, SearchPost.class);
            return response.hits().hits().stream()
                    .filter(hit -> Objects.nonNull(hit.source()))
                    .map(this::toSearchPostHit)
                    .toList();
        } catch (Exception e) {
            throw new FailedToSearchPostsException(e);
        }
    }

    private OpenSearchPostHit toSearchPostHit(Hit<SearchPost> hit) {
        return new OpenSearchPostHit(hit.source(), hit.score(), hit.sort());
    }
}
