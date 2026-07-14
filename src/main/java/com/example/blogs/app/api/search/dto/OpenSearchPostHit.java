package com.example.blogs.app.api.search.dto;

import org.opensearch.client.opensearch._types.FieldValue;

import java.util.List;

public record OpenSearchPostHit(
        SearchPost post,
        Double score,
        List<FieldValue> sortValues
) {
}
