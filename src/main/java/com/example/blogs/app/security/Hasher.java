package com.example.blogs.app.security;

/**
 * Cryptographic hashing service for generating irreversible token digests.
 */
public interface Hasher {
    /**
     * Generates a SHA-256 hash of the input string.
     *
     * @param input plaintext string to hash
     * @return hexadecimal representation of the hash
     */
    String hash(String input);
}
