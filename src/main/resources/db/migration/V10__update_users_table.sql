ALTER TABLE users
    ADD COLUMN file_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT fk_file FOREIGN KEY (file_id) REFERENCES files (id) ON DELETE SET NULL;

CREATE INDEX fk_users_file ON users (file_id);

ALTER TABLE users
    DROP COLUMN profile_picture_url;