CREATE TABLE revoked_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(64) NOT NULL UNIQUE,
    revoked_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP   NOT NULL
);

CREATE INDEX idx_revoked_tokens_expires_at
    ON revoked_tokens (expires_at);