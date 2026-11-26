ALTER TABLE accounts
    ADD CONSTRAINT chk_accounts_balance_non_negative CHECK (balance >= 0),
    ADD CONSTRAINT chk_accounts_reserved_non_negative CHECK (reserved_balance >= 0),
    ADD CONSTRAINT chk_accounts_available_non_negative CHECK (balance >= reserved_balance);

CREATE TABLE holdings
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    stock_id      BIGINT    NOT NULL REFERENCES stocks (id) ON DELETE CASCADE,
    quantity      INT       NOT NULL CHECK (quantity > 0),
    average_price BIGINT    NOT NULL CHECK (average_price >= 0),
    updated_at    TIMESTAMP NOT NULL,
    UNIQUE (user_id, stock_id)
);

CREATE TABLE orders
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    stock_id        BIGINT      NOT NULL REFERENCES stocks (id) ON DELETE CASCADE,
    side            VARCHAR(10) NOT NULL,
    quantity        INT         NOT NULL CHECK (quantity > 0),
    requested_price BIGINT      NOT NULL,
    executed_price  BIGINT,
    status          VARCHAR(20) NOT NULL,
    created_at      TIMESTAMP   NOT NULL,
    executed_at     TIMESTAMP
);

CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_user_stock_status ON orders (user_id, stock_id, status);

CREATE TABLE transactions
(
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT      NOT NULL UNIQUE REFERENCES orders (id),
    user_id      BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    stock_id     BIGINT      NOT NULL REFERENCES stocks (id) ON DELETE CASCADE,
    side         VARCHAR(10) NOT NULL,
    quantity     INT         NOT NULL CHECK (quantity > 0),
    price        BIGINT      NOT NULL,
    total_amount BIGINT      NOT NULL,
    created_at   TIMESTAMP   NOT NULL
);