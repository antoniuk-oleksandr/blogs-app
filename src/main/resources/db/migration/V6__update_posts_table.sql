ALTER TABLE posts
    ADD COLUMN file_id BIGINT;

ALTER TABLE posts
    ADD CONSTRAINT fk_file FOREIGN KEY (file_id) REFERENCES files (id) ON DELETE SET NULL;

CREATE INDEX fk_posts_file ON posts (file_id);

ALTER TABLE posts
    DROP COLUMN preview_image_url;