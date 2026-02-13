package com.example.blogs.app.api.reaction.exception;

public class UserHasAlreadyReactedException extends RuntimeException {

    public UserHasAlreadyReactedException(Exception cause) {
        super("User has already reacted", cause);
    }
}
