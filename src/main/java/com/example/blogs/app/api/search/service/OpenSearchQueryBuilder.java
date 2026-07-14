package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.search.dto.CursorData;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenSearchQueryBuilder {
    private static final String INDEX = "posts";

    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_ID = "id";
    private static final String FIELD_LIKES_COUNT = "likesCount";
    private static final String FIELD_COMMENTS_COUNT = "commentsCount";

    public SearchRequest buildRelevanceQuery(String query, CursorData cursorData, int size) {
        List<FieldValue> searchAfter = searchAfter(cursorData);
        return SearchRequest.of(r -> {
            var builder = r
                    .index(INDEX)
                    .size(size)
                    .query(q -> q.functionScore(buildFunctionScoreQuery(query)))
                    .sort(s -> s.score(sc -> sc.order(SortOrder.Desc)))
                    .sort(s -> s.field(f -> f.field(FIELD_ID).order(SortOrder.Asc)));
            if (!searchAfter.isEmpty()) {
                builder.searchAfter(searchAfter);
            }
            return builder;
        });
    }

    public SearchRequest buildNewestQuery(String query, CursorData cursorData, int size) {
        List<FieldValue> searchAfter = searchAfter(cursorData);
        return SearchRequest.of(r -> {
            var builder = r
                    .index(INDEX)
                    .size(size)
                    .query(q -> q.multiMatch(buildMultiMatchQuery(query)))
                    .sort(s -> s.field(f -> f.field(FIELD_CREATED_AT).order(SortOrder.Desc)))
                    .sort(s -> s.field(f -> f.field(FIELD_ID).order(SortOrder.Asc)));
            if (!searchAfter.isEmpty()) {
                builder.searchAfter(searchAfter);
            }
            return builder;
        });
    }

    public SearchRequest buildOldestQuery(String query, CursorData cursorData, int size) {
        List<FieldValue> searchAfter = searchAfter(cursorData);
        return SearchRequest.of(r -> {
            var builder = r
                    .index(INDEX)
                    .size(size)
                    .query(q -> q.multiMatch(buildMultiMatchQuery(query)))
                    .sort(s -> s.field(f -> f.field(FIELD_CREATED_AT).order(SortOrder.Asc)))
                    .sort(s -> s.field(f -> f.field(FIELD_ID).order(SortOrder.Asc)));
            if (!searchAfter.isEmpty()) {
                builder.searchAfter(searchAfter);
            }
            return builder;
        });
    }

    public FunctionScoreQuery buildFunctionScoreQuery(String query) {
        return FunctionScoreQuery.of(fs -> fs
                .query(q -> q.multiMatch(buildMultiMatchQuery(query)))
                .functions(List.of(likesScoreFn(), commentsScoreFn(), recencyDecayFn()))
                .scoreMode(FunctionScoreMode.Sum)
                .boostMode(FunctionBoostMode.Sum)
        );
    }

    public MultiMatchQuery buildMultiMatchQuery(String query) {
        return new MultiMatchQuery.Builder()
                .query(query)
                .fields(List.of("title^3", "description", "content"))
                .fuzziness("AUTO")
                .build();
    }

    public FunctionScore likesScoreFn() {
        return new FunctionScore.Builder()
                .fieldValueFactor(fvf -> fvf
                        .field(FIELD_LIKES_COUNT)
                        .factor(1.5F)
                        .modifier(FieldValueFactorModifier.Sqrt)
                        .missing(1.0)
                )
                .build();
    }

    public FunctionScore commentsScoreFn() {
        return new FunctionScore.Builder()
                .fieldValueFactor(fvf -> fvf
                        .field(FIELD_COMMENTS_COUNT)
                        .factor(2F)
                        .modifier(FieldValueFactorModifier.Log1p)
                        .missing(0.0)
                )
                .build();
    }

    public FunctionScore recencyDecayFn() {
        return new FunctionScore.Builder()
                .exp(exp -> exp
                        .field(FIELD_CREATED_AT)
                        .placement(p -> p
                                .origin(JsonData.of("now"))
                                .scale(JsonData.of("7d"))
                                .decay(0.5)
                        )
                )
                .build();
    }

    private List<FieldValue> searchAfter(CursorData cursorData) {
        if (cursorData == null || cursorData.sortValues() == null || cursorData.sortValues().isEmpty()) {
            return List.of();
        }

        return cursorData.sortValues().stream()
                .map(this::toFieldValue)
                .toList();
    }

    private FieldValue toFieldValue(Object value) {
        if (value instanceof Integer integer) {
            return FieldValue.of(integer.longValue());
        }
        if (value instanceof Long longValue) {
            return FieldValue.of(longValue);
        }
        if (value instanceof Float floatValue) {
            return FieldValue.of(floatValue.doubleValue());
        }
        if (value instanceof Double doubleValue) {
            return FieldValue.of(doubleValue);
        }
        if (value instanceof Boolean booleanValue) {
            return FieldValue.of(booleanValue);
        }
        return FieldValue.of(String.valueOf(value));
    }
}
