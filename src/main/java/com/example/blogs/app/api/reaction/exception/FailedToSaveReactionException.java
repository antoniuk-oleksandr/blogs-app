package com.example.blogs.app.api.reaction.exception;

public class FailedToSaveReactionException extends RuntimeException{

    public FailedToSaveReactionException(Exception cause) {
        super("Failed to save reaction", cause);
    }
}
