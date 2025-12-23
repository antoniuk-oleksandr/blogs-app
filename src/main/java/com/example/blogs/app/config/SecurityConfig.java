package com.example.blogs.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures Spring Security for a stateless JWT-based API.
 * CSRF protection is disabled as appropriate for stateless REST APIs.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Constructs a new SecurityConfig.
     * Spring automatically instantiates this configuration bean.
     */
    public SecurityConfig() {
    }

    /**
     * Configures the security filter chain with stateless session management.
     * Currently, permits all requests - authentication will be added later.
     *
     * @param http the HttpSecurity to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
