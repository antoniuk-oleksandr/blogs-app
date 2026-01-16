package com.example.blogs.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HasherImplTest {

    @Mock
    private MessageDigest messageDigest;

    private Hasher hasher;

    @BeforeEach
    void setUp() {
        hasher = new HasherImpl(messageDigest);
    }

    @Test
    void hash_shouldReturnExpectedHash() {
        byte[] bytes = "hash".getBytes(StandardCharsets.UTF_8);
        when(messageDigest.digest(any(byte[].class))).thenReturn(bytes);
        String expectedHex = HexFormat.of().formatHex(bytes);

        String hash = hasher.hash("hash");

        assertThat(hash).isEqualTo(expectedHex);
    }

    @Test
    void hash_shouldThrowIllegalStateException_whenMessageDigestFails() {
        when(messageDigest.digest(any(byte[].class)))
                .thenThrow(new RuntimeException("Digest error"));

        assertThatThrownBy(() -> hasher.hash("test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Hashing failed")
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
