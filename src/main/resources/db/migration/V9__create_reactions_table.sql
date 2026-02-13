CREATE TABLE reactions
(
    id            BIGSERIAL PRIMARY KEY,
    post_id       BIGINT      NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    user_id       BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    reaction_type VARCHAR(10) NOT NULL CHECK (reaction_type IN ('LIKE', 'DISLIKE')) DEFAULT 'LIKE',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),

    UNIQUE (post_id, user_id)
);

CREATE INDEX idx_post_reactions_post_id ON reactions (post_id);
CREATE INDEX idx_post_reactions_user_id ON reactions (user_id);