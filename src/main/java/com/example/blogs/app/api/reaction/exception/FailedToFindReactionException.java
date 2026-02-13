package com.example.blogs.app.api.reaction.exception;

public class FailedToFindReactionException extends RuntimeException {

    public FailedToFindReactionException(Exception cause) {
        super("Failed to find reaction", cause);
    }
}
