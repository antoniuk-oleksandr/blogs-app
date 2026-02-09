package com.example.blogs.app.logging;

/**
 * Defines standard MDC (Mapped Diagnostic Context) keys for consistent structured logging across the application.
 */
public final class MDCKeys {

    /**
     * The key for the unique identifier of the request, used to trace logs related to a specific request across different components and services.
     */
    public static final String REQUEST_ID = "requestId";

    /**
     * The key for the user identifier, used to associate logs with a specific user, which can be helpful for debugging and monitoring user-related activities.
     */
    public static final String USER_ID = "userId";

    private MDCKeys() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}
