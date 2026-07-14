package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.search.dto.CursorData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OpenSearchQueryBuilderTest {

    private OpenSearchQueryBuilder queryBuilder;

    @BeforeEach
    void setUp() {
        queryBuilder = new OpenSearchQueryBuilder();
    }

    @Test
    void buildRelevanceQuery_shouldReturnSearchRequestWithPostsIndex() {
        SearchRequest request = queryBuilder.buildRelevanceQuery("spring boot", null, 21);

        assertThat(request.index()).containsExactly("posts");
    }

    @Test
    void buildRelevanceQuery_shouldReturnFunctionScoreQuery() {
        SearchRequest request = queryBuilder.buildRelevanceQuery("spring boot", null, 21);

        assertThat(request.query()).isNotNull();
        assertThat(request.query().isFunctionScore()).isTrue();
    }

    @Test
    void buildRelevanceQuery_shouldReturnContentField() {
        SearchRequest request = queryBuilder.buildRelevanceQuery("spring boot", null, 21);

        assertThat(request.source()).isNull();
    }

    @Test
    void buildRelevanceQuery_shouldSetSearchAfter_whenCursorDataIsProvided() {
        CursorData cursorData = new CursorData(List.of(1, 2L, 3.5F, 4.5D, true, "slug"));

        SearchRequest request = queryBuilder.buildRelevanceQuery("spring boot", cursorData, 21);

        assertThat(request.searchAfter()).hasSize(6);
        assertThat(request.searchAfter().get(0).longValue()).isEqualTo(1L);
        assertThat(request.searchAfter().get(1).longValue()).isEqualTo(2L);
        assertThat(request.searchAfter().get(2).doubleValue()).isEqualTo(3.5D);
        assertThat(request.searchAfter().get(3).doubleValue()).isEqualTo(4.5D);
        assertThat(request.searchAfter().get(4).booleanValue()).isTrue();
        assertThat(request.searchAfter().get(5).stringValue()).isEqualTo("slug");
    }

    @Test
    void buildNewestQuery_shouldReturnSearchRequestWithPostsIndex() {
        SearchRequest request = queryBuilder.buildNewestQuery("java", null, 21);

        assertThat(request.index()).containsExactly("posts");
    }

    @Test
    void buildNewestQuery_shouldReturnMultiMatchQuery() {
        SearchRequest request = queryBuilder.buildNewestQuery("java", null, 21);

        assertThat(request.query()).isNotNull();
        assertThat(request.query().isMultiMatch()).isTrue();
    }

    @Test
    void buildNewestQuery_shouldSortByCreatedAtDescending() {
        SearchRequest request = queryBuilder.buildNewestQuery("java", null, 21);

        assertThat(request.sort()).hasSize(2);
        assertThat(request.sort().getFirst().isField()).isTrue();
        assertThat(request.sort().getFirst().field().field()).isEqualTo("createdAt");
        assertThat(request.sort().getFirst().field().order()).isEqualTo(SortOrder.Desc);
    }

    @Test
    void buildOldestQuery_shouldReturnSearchRequestWithPostsIndex() {
        SearchRequest request = queryBuilder.buildOldestQuery("java", null, 21);

        assertThat(request.index()).containsExactly("posts");
    }

    @Test
    void buildOldestQuery_shouldReturnMultiMatchQuery() {
        SearchRequest request = queryBuilder.buildOldestQuery("java", null, 21);

        assertThat(request.query()).isNotNull();
        assertThat(request.query().isMultiMatch()).isTrue();
    }

    @Test
    void buildOldestQuery_shouldSortByCreatedAtAscending() {
        SearchRequest request = queryBuilder.buildOldestQuery("java", null, 21);

        assertThat(request.sort()).hasSize(2);
        assertThat(request.sort().getFirst().isField()).isTrue();
        assertThat(request.sort().getFirst().field().field()).isEqualTo("createdAt");
        assertThat(request.sort().getFirst().field().order()).isEqualTo(SortOrder.Asc);
    }

    @Test
    void buildMultiMatchQuery_shouldSetQueryTermAndSearchFields() {
        MultiMatchQuery query = queryBuilder.buildMultiMatchQuery("spring");

        assertThat(query.query()).isEqualTo("spring");
        assertThat(query.fields()).containsExactlyInAnyOrder("title^3", "description", "content");
    }

    @Test
    void buildMultiMatchQuery_shouldSetFuzzinessToAuto() {
        MultiMatchQuery query = queryBuilder.buildMultiMatchQuery("spring");

        assertThat(query.fuzziness()).isEqualTo("AUTO");
    }

    @Test
    void buildFunctionScoreQuery_shouldHaveThreeScoringFunctions() {
        FunctionScoreQuery query = queryBuilder.buildFunctionScoreQuery("java");

        assertThat(query.functions()).hasSize(3);
    }

    @Test
    void buildFunctionScoreQuery_shouldUseSumScoreModeAndBoostMode() {
        FunctionScoreQuery query = queryBuilder.buildFunctionScoreQuery("java");

        assertThat(query.scoreMode()).isEqualTo(FunctionScoreMode.Sum);
        assertThat(query.boostMode()).isEqualTo(FunctionBoostMode.Sum);
    }

    @Test
    void likesScoreFn_shouldReturnFieldValueFactorForLikesWithSqrtModifier() {
        FunctionScore fn = queryBuilder.likesScoreFn();

        assertThat(fn.fieldValueFactor().field()).isEqualTo("likesCount");
        assertThat(fn.fieldValueFactor().factor()).isEqualTo(1.5F);
        assertThat(fn.fieldValueFactor().modifier()).isEqualTo(FieldValueFactorModifier.Sqrt);
    }

    @Test
    void commentsScoreFn_shouldReturnFieldValueFactorForCommentsWithLog1pModifier() {
        FunctionScore fn = queryBuilder.commentsScoreFn();

        assertThat(fn.fieldValueFactor().field()).isEqualTo("commentsCount");
        assertThat(fn.fieldValueFactor().factor()).isEqualTo(2F);
        assertThat(fn.fieldValueFactor().modifier()).isEqualTo(FieldValueFactorModifier.Log1p);
    }

    @Test
    void recencyDecayFn_shouldReturnExpDecayFunctionForCreatedAt() {
        FunctionScore fn = queryBuilder.recencyDecayFn();

        assertThat(fn.exp().field()).isEqualTo("createdAt");
        assertThat(fn.exp().placement().decay()).isEqualTo(0.5);
    }

    @Test
    void buildNewestQuery_shouldReturnContentField() {
        SearchRequest request = queryBuilder.buildNewestQuery("java", null, 21);

        assertThat(request.source()).isNull();
    }

    @Test
    void buildOldestQuery_shouldReturnContentField() {
        SearchRequest request = queryBuilder.buildOldestQuery("java", null, 21);

        assertThat(request.source()).isNull();
    }
}
