ALTER TABLE stocks
    ADD CONSTRAINT chk_stocks_current_price
        CHECK (current_price >= 0),
    ADD CONSTRAINT chk_stocks_previous_close
        CHECK (previous_close >= 0);

ALTER TABLE orders
    ADD CONSTRAINT chk_orders_requested_price
        CHECK (requested_price > 0),
    ADD CONSTRAINT chk_orders_executed_price
        CHECK (executed_price IS NULL OR executed_price > 0);

ALTER TABLE transactions
    ADD CONSTRAINT chk_transactions_price
        CHECK (price > 0),
    ADD CONSTRAINT chk_transactions_total_amount
        CHECK (total_amount > 0);

CREATE INDEX idx_transactions_user_created_at
    ON transactions (user_id, created_at DESC);

CREATE INDEX idx_orders_user_created_at
    ON orders (user_id, created_at DESC);

CREATE INDEX idx_holdings_user_id
    ON holdings (user_id);
