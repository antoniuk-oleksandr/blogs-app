package com.example.blogs.app.util;

import com.example.blogs.app.api.search.dto.CursorData;
import com.example.blogs.app.api.search.exception.FailedToDecodeCursorException;
import com.example.blogs.app.api.search.exception.FailedToEncodeCursorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CursorUtilsTest {

    @Test
    void encode_shouldReturnUrlSafeBase64Cursor() {
        CursorUtils cursorUtils = new CursorUtils(new ObjectMapper());
        CursorData cursorData = new CursorData(List.of(1.5, 10));

        String cursor = cursorUtils.encode(cursorData);

        assertThat(cursor)
                .isNotBlank()
                .doesNotContain("+", "/");
    }

    @Test
    void decode_shouldReturnCursorData_whenCursorIsValid() {
        CursorUtils cursorUtils = new CursorUtils(new ObjectMapper());
        String cursor = cursorUtils.encode(new CursorData(List.of(1.5, 10)));

        CursorData result = cursorUtils.decode(cursor);

        assertThat(result.sortValues()).containsExactly(1.5, 10);
    }

    @Test
    void decode_shouldReturnNull_whenCursorIsNull() {
        CursorUtils cursorUtils = new CursorUtils(new ObjectMapper());

        CursorData result = cursorUtils.decode(null);

        assertThat(result).isNull();
    }

    @Test
    void decode_shouldReturnNull_whenCursorIsBlank() {
        CursorUtils cursorUtils = new CursorUtils(new ObjectMapper());

        CursorData result = cursorUtils.decode(" ");

        assertThat(result).isNull();
    }

    @Test
    void decode_shouldThrowFailedToDecodeCursorException_whenCursorIsInvalid() {
        CursorUtils cursorUtils = new CursorUtils(new ObjectMapper());

        assertThatThrownBy(() -> cursorUtils.decode("not-valid-base64"))
                .isInstanceOf(FailedToDecodeCursorException.class);
    }

    @Test
    void encode_shouldThrowFailedToEncodeCursorException_whenSerializationFails() throws Exception {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        CursorData cursorData = new CursorData(List.of(1));
        when(objectMapper.writeValueAsString(cursorData.sortValues()))
                .thenThrow(new JsonProcessingException("Serialization failed") {
                });

        CursorUtils cursorUtils = new CursorUtils(objectMapper);

        assertThatThrownBy(() -> cursorUtils.encode(cursorData))
                .isInstanceOf(FailedToEncodeCursorException.class);
    }
}
