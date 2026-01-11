package com.example.blogs.app.api.auth.exception;

public class TokenAlreadyRevokedException extends RuntimeException {
    public TokenAlreadyRevokedException() {
        super("Token is already revoked.");
    }
}
