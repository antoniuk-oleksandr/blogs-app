package com.example.blogs.app.util;

import com.example.blogs.app.api.search.dto.CursorData;
import com.example.blogs.app.api.search.exception.FailedToDecodeCursorException;
import com.example.blogs.app.api.search.exception.FailedToEncodeCursorException;
import com.example.blogs.app.logging.MDCKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
@AllArgsConstructor
public class CursorUtils {

    private final Logger log = LoggerFactory.getLogger(CursorUtils.class);

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String encode(CursorData cursorData) {
        try {
            String json = objectMapper.writeValueAsString(cursorData.sortValues());

            return Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("cursor_encoding_failed cursorData={} requestId={}", cursorData, MDC.get(MDCKeys.REQUEST_ID));
            throw new FailedToEncodeCursorException(e);
        }
    }

    @SneakyThrows
    public CursorData decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        try {
            String decodedJson = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            List<Object> sortValues = objectMapper.readValue(
                    decodedJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class)
            );

            return new CursorData(sortValues);
        } catch (Exception e) {
            log.error("cursor_decoding_failed cursor={} requestId={}", cursor, MDC.get(MDCKeys.REQUEST_ID));
            throw new FailedToDecodeCursorException(e);
        }
    }
}
