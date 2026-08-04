CREATE TABLE users
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,

    login      VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,

    role       VARCHAR(20)  NOT NULL,
    status     VARCHAR(20)  NOT NULL,

    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ,
    blocked_at TIMESTAMPTZ,

    CONSTRAINT pk_users
        PRIMARY KEY (id),

    CONSTRAINT uq_users_login
        UNIQUE (login),

    CONSTRAINT chk_users_login_length
        CHECK (char_length(login) BETWEEN 3 AND 50),

    CONSTRAINT chk_users_role
        CHECK (role IN ('USER', 'ADMIN')),

    CONSTRAINT chk_users_status
        CHECK (status IN ('ACTIVE', 'BLOCKED', 'DELETED'))
);