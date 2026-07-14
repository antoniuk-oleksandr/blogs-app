package com.example.blogs.app.api.search.service;

import com.example.blogs.app.api.search.dto.CursorData;
import com.example.blogs.app.api.search.dto.OpenSearchPostHit;
import com.example.blogs.app.api.search.dto.SearchPost;
import com.example.blogs.app.api.search.dto.SearchPostsResponseDTO;
import com.example.blogs.app.api.search.dto.SearchType;
import com.example.blogs.app.api.search.exception.FailedToDecodeCursorException;
import com.example.blogs.app.api.search.exception.FailedToEncodeCursorException;
import com.example.blogs.app.api.search.exception.FailedToSearchPostsException;
import com.example.blogs.app.api.search.exception.InvalidCursorException;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.util.CursorUtils;
import lombok.AllArgsConstructor;
import org.opensearch.client.opensearch._types.FieldValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OpenSearchService implements SearchService {
    private static final int PAGE_SIZE = 20;

    private final OpenSearchQueryExecutor queryExecutor;

    private final OpenSearchQueryBuilder queryBuilder;

    private final CursorUtils cursorUtils;

    private static final Logger log = LoggerFactory.getLogger(OpenSearchService.class);

    @Override
    public SearchPostsResponseDTO searchPosts(String query, SearchType type, String cursor) {
        try {
            CursorData cursorData = cursorUtils.decode(cursor);

            List<OpenSearchPostHit> hits = switch (type) {
                case RELEVANCE -> queryExecutor.execute(queryBuilder.buildRelevanceQuery(query, cursorData, PAGE_SIZE + 1));
                case NEWEST -> queryExecutor.execute(queryBuilder.buildNewestQuery(query, cursorData, PAGE_SIZE + 1));
                case OLDEST -> queryExecutor.execute(queryBuilder.buildOldestQuery(query, cursorData, PAGE_SIZE + 1));
            };
            boolean hasMore = hits.size() > PAGE_SIZE;
            List<OpenSearchPostHit> pageHits = hits.stream()
                    .limit(PAGE_SIZE)
                    .toList();
            List<SearchPost> posts = pageHits.stream()
                    .map(OpenSearchPostHit::post)
                    .toList();

            log.info("posts_searched type={} resultCount={} requestId={}",
                    type, posts.size(), MDC.get(MDCKeys.REQUEST_ID));

            String nextCursor = hasMore
                    ? cursorUtils.encode(new CursorData(toCursorValues(pageHits.getLast().sortValues())))
                    : null;
            return new SearchPostsResponseDTO(posts, nextCursor, hasMore);
        } catch (FailedToDecodeCursorException | FailedToEncodeCursorException e) {
            throw new InvalidCursorException(e);
        } catch (Exception e) {
            log.error("failed_to_search_posts type={} error={} requestId={}",
                    type, e, MDC.get(MDCKeys.REQUEST_ID));
            throw new FailedToSearchPostsException(e);
        }
    }

    private List<Object> toCursorValues(List<FieldValue> sortValues) {
        return sortValues.stream()
                .map(this::toCursorValue)
                .toList();
    }

    private Object toCursorValue(FieldValue value) {
        return switch (value._kind()) {
            case Double -> value.doubleValue();
            case Long -> value.longValue();
            case Boolean -> value.booleanValue();
            case String -> value.stringValue();
            default -> value.toString();
        };
    }
}
