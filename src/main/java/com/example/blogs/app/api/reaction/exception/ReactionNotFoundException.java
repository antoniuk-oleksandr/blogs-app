package com.example.blogs.app.api.reaction.exception;

public class ReactionNotFoundException extends  RuntimeException {

    public ReactionNotFoundException(Exception cause) {
        super("Reaction not found", cause);
    }
}
