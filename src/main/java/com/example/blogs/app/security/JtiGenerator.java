package com.example.blogs.app.security;

/**
 * Generates unique JWT Token Identifiers (JTI) for use as the subject claim in JWTs.
 * The JTI serves as a UUID-based unique identifier for each token, separate from the user ID.
 */
public interface JtiGenerator {
    /**
     * Generates a unique JWT Token Identifier.
     *
     * @return a UUID string to be used as the JWT subject (sub) claim
     */
    String generateJti();
}
