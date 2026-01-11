package com.example.blogs.app.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * SHA-256 based hashing implementation for creating one-way token digests.
 * Uses UTF-8 encoding and hexadecimal output format.
 */
@Component
@AllArgsConstructor
public class HasherImpl implements Hasher {

    @Override
    public String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
