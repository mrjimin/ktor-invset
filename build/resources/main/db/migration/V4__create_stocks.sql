CREATE TABLE stocks
(
    id             BIGSERIAL PRIMARY KEY,
    code           VARCHAR(20)  NOT NULL UNIQUE,
    name           VARCHAR(100) NOT NULL,
    description    TEXT,
    current_price  BIGINT       NOT NULL,
    previous_close BIGINT       NOT NULL,
    listed         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
);

CREATE TABLE stock_prices
(
    id         BIGSERIAL PRIMARY KEY,
    stock_id   BIGINT    NOT NULL REFERENCES stocks (id) ON DELETE CASCADE,
    date       DATE      NOT NULL,
    open       BIGINT    NOT NULL,
    high       BIGINT    NOT NULL,
    low        BIGINT    NOT NULL,
    close      BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (stock_id, date)
);