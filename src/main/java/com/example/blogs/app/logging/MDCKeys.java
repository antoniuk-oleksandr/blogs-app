package com.example.blogs.app.logging;

public final class MDCKeys {
    public static final String REQUEST_ID = "requestId";
    public static final String USER_ID = "userId";

    private MDCKeys() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}
