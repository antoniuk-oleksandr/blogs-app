package com.example.blogs.app.config;

import com.example.blogs.app.exception.JwtAuthenticationEntryPoint;
import com.example.blogs.app.security.JWTToUserPrincipalConverter;
import com.example.blogs.app.security.JwtExceptionFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Configures Spring Security for a stateless JWT-based API.
 * CSRF protection is disabled as appropriate for stateless REST APIs.
 */
@Configuration
@AllArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtExceptionFilter jwtExceptionFilter;

    /**
     * Configures the security filter chain with JWT-based stateless authentication.
     * Protects /auth/me endpoint and permits all other requests.
     *
     * @param http the HttpSecurity to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtExceptionFilter, BearerTokenAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/posts/{postId}").authenticated()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new JWTToUserPrincipalConverter()))
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                ).exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );

        return http.build();
    }

    /**
     * Provides a SHA-256 MessageDigest bean for token hashing.
     *
     * @return SHA-256 MessageDigest instance
     * @throws IllegalStateException if SHA-256 algorithm is not available
     */
    @Bean
    public MessageDigest messageDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
