package com.example.blogs.app.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Hashes strings using SHA-256 algorithm and returns hexadecimal representation.
 */
@Component
@AllArgsConstructor
public class HasherImpl implements Hasher {

    private final MessageDigest messageDigest;

    /**
     * Hashes the input string using SHA-256 and returns hex-encoded result.
     *
     * @param input the string to hash
     * @return hexadecimal representation of the hash
     * @throws IllegalStateException if hashing operation fails
     */
    @Override
    public String hash(String input) {
        try {
            byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }
}
