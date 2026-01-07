package com.example.blogs.app.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Default implementation of JtiGenerator that produces UUID-based token identifiers.
 */
@Component
public class JtiGeneratorImpl implements JtiGenerator {

    /**
     * Generates a unique JWT Token Identifier using UUID.randomUUID().
     *
     * @return a randomly generated UUID string
     */
    @Override
    public String generateJti() {
        return UUID.randomUUID().toString();
    }
}
