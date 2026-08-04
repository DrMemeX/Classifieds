CREATE TABLE user_profiles
(
    id         BIGINT,

    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(20)  NOT NULL,

    CONSTRAINT pk_user_profiles
        PRIMARY KEY (id),

    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (id)
            REFERENCES users (id),

    CONSTRAINT uq_user_profiles_phone
        UNIQUE (phone),

    CONSTRAINT chk_user_profiles_first_name_length
        CHECK (char_length(first_name) BETWEEN 1 AND 100),

    CONSTRAINT chk_user_profiles_last_name_length
        CHECK (char_length(last_name) BETWEEN 1 AND 100)
);