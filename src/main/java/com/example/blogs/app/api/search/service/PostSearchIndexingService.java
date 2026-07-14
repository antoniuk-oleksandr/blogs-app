package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.indexing.PostSearchIndexAction;
import com.example.blogs.app.api.search.indexing.PostSearchIndexEvent;
import com.example.blogs.app.api.search.mapper.PostSearchDocumentMapper;
import lombok.AllArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostSearchIndexingService {
    private static final String INDEX = "posts";

    private static final Logger log = LoggerFactory.getLogger(PostSearchIndexingService.class);

    private final PostRepositoryAdapter postRepositoryAdapter;

    private final PostSearchDocumentMapper documentMapper;

    private final OpenSearchClient openSearchClient;

    @Transactional(readOnly = true)
    public void indexBatch(List<PostSearchIndexEvent> events) {
        if (events.isEmpty()) {
            return;
        }

        List<BulkOperation> operations = new ArrayList<>();
        operations.addAll(deleteOperations(events));
        operations.addAll(upsertOperations(events));

        if (operations.isEmpty()) {
            return;
        }

        try {
            BulkRequest request = BulkRequest.of(b -> b.index(INDEX).operations(operations));
            BulkResponse response = openSearchClient.bulk(request);
            if (response.errors()) {
                throw new IllegalStateException("OpenSearch bulk indexing completed with item errors");
            }
            log.info("posts_indexed eventCount={} operationCount={}", events.size(), operations.size());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to bulk index posts", e);
        }
    }

    private List<BulkOperation> deleteOperations(List<PostSearchIndexEvent> events) {
        return events.stream()
                .filter(event -> event.action() == PostSearchIndexAction.DELETE)
                .map(PostSearchIndexEvent::postId)
                .distinct()
                .map(postId -> BulkOperation.of(operation -> operation
                        .delete(delete -> delete.id(String.valueOf(postId)))))
                .toList();
    }

    private List<BulkOperation> upsertOperations(List<PostSearchIndexEvent> events) {
        List<Long> postIds = events.stream()
                .filter(event -> event.action() == PostSearchIndexAction.UPSERT)
                .map(PostSearchIndexEvent::postId)
                .distinct()
                .toList();

        if (postIds.isEmpty()) {
            return List.of();
        }

        Map<Long, PostEntity> postsById = postRepositoryAdapter.findAllByIdIn(postIds).stream()
                .collect(Collectors.toMap(PostEntity::getId, Function.identity()));

        return postIds.stream()
                .map(postsById::get)
                .filter(Objects::nonNull)
                .map(documentMapper::toSearchPost)
                .map(this::indexOperation)
                .toList();
    }

    private BulkOperation indexOperation(SearchPost post) {
        return BulkOperation.of(operation -> operation
                .index(index -> index
                        .id(String.valueOf(post.id()))
                        .document(post)));
    }
}
