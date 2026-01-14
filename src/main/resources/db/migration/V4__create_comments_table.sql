CREATE TABLE comments
(
    id         BIGSERIAL PRIMARY KEY,
    content    TEXT                                NOT NULL,
    post_id    BIGINT                              NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    author_id  BIGINT                              NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_edited  BOOLEAN   DEFAULT FALSE             NOT NULL
);

CREATE index idx_comments_post_id
    ON comments (post_id);

CREATE index idx_comments_author_id
    ON comments (author_id);

CREATE TRIGGER update_comments_updated_at
    BEFORE UPDATE
    ON comments
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();