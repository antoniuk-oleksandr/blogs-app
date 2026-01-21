CREATE TABLE files
(
    id             BIGSERIAL PRIMARY KEY,
    uuid           VARCHAR(36)  NOT NULL UNIQUE,
    file_path      VARCHAR(255) NOT NULL,
    file_name      VARCHAR(255) NOT NULL,
    file_extension VARCHAR(255) NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);