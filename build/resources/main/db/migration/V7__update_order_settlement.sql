ALTER TABLE orders
    ADD COLUMN reserved_amount BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN fee_rate NUMERIC(10, 6) NOT NULL DEFAULT 0,
    ADD COLUMN tax_rate NUMERIC(10, 6) NOT NULL DEFAULT 0,
    ADD COLUMN fee_amount BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN tax_amount BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN cancelled_at TIMESTAMP;

ALTER TABLE orders
    ADD CONSTRAINT chk_orders_reserved_amount
        CHECK (reserved_amount >= 0),
    ADD CONSTRAINT chk_orders_fee_rate
        CHECK (fee_rate >= 0),
    ADD CONSTRAINT chk_orders_tax_rate
        CHECK (tax_rate >= 0),
    ADD CONSTRAINT chk_orders_fee_amount
        CHECK (fee_amount >= 0),
    ADD CONSTRAINT chk_orders_tax_amount
        CHECK (tax_amount >= 0);

CREATE INDEX idx_orders_user_status_created_at
    ON orders (user_id, status, created_at DESC);

ALTER TABLE transactions
    ADD COLUMN fee_amount BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN tax_amount BIGINT NOT NULL DEFAULT 0;