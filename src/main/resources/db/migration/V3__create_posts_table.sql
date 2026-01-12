CREATE TABLE posts
(
    id                BIGSERIAL PRIMARY KEY,
    title             VARCHAR(255) NOT NULL,
    description       VARCHAR(255) NOT NULL,
    content           TEXT         NOT NULL,
    author_id         BIGINT       NOT NULL REFERENCES users (id),
    slug              VARCHAR(255) NOT NULL UNIQUE,
    preview_image_url TEXT         NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_posts_updated_at
    BEFORE UPDATE
    ON posts
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();