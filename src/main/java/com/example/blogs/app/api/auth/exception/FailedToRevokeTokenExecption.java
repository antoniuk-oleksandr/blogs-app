package com.example.blogs.app.api.auth.exception;

public class FailedToRevokeTokenExecption extends RuntimeException {
    public FailedToRevokeTokenExecption() {
        super("Failed to revoke token");
    }
}
