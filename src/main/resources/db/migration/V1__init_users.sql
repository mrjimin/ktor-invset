CREATE TABLE users
(
    id             BIGSERIAL PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    nickname       VARCHAR(50)  NOT NULL,
    type           VARCHAR(20)  NOT NULL,
    role           VARCHAR(20)  NOT NULL DEFAULT 'USER',
    student_number VARCHAR(5) UNIQUE,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL
);