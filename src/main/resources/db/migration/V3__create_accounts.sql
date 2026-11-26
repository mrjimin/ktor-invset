CREATE TABLE accounts
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT    NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    initial_balance  BIGINT    NOT NULL DEFAULT 1000000,
    balance          BIGINT    NOT NULL DEFAULT 1000000,
    reserved_balance BIGINT    NOT NULL DEFAULT 0,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);