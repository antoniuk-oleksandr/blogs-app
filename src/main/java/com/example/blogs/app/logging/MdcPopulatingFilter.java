package com.example.blogs.app.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Populates MDC with authenticated user information for request-scoped logging context.
 */
@Component
@Order(2)
public class MdcPopulatingFilter extends OncePerRequestFilter {

    /**
     * Extracts authenticated user ID from security context and populates MDC for use in log messages.
     * Runs after authentication is established but before business logic executes.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if request processing fails
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )
            throws ServletException, IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                String userId = authentication.getName();
                MDC.put(MDCKeys.USER_ID, userId);
            }

            filterChain.doFilter(request, response);
        } finally {
            // MDC will be cleared by RequestLoggingFilter
        }
    }
}
