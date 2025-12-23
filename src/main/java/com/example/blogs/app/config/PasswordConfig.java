package com.example.blogs.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Provides BCrypt password encoding for secure password hashing.
 */
@Configuration
public class PasswordConfig {

    /**
     * Constructs a new PasswordConfig.
     * Spring automatically instantiates this configuration bean.
     */
    public PasswordConfig() {
    }

    /**
     * Creates a BCrypt password encoder bean for password hashing.
     *
     * @return BCrypt password encoder with default strength (10 rounds)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
