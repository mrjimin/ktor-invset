CREATE TABLE tax_collections
(
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT         NOT NULL UNIQUE REFERENCES orders (id),
    user_id     BIGINT         NOT NULL REFERENCES users (id),
    stock_id    BIGINT         NOT NULL REFERENCES stocks (id),
    tax_rate    NUMERIC(10, 6) NOT NULL,
    tax_amount  BIGINT         NOT NULL CHECK (tax_amount > 0),
    created_at  TIMESTAMP      NOT NULL,

    CONSTRAINT chk_tax_collections_tax_rate
        CHECK (tax_rate >= 0)
);

CREATE INDEX idx_tax_collections_created_at
    ON tax_collections (created_at DESC);

CREATE INDEX idx_tax_collections_user_id
    ON tax_collections (user_id);

CREATE INDEX idx_tax_collections_stock_id
    ON tax_collections (stock_id);
