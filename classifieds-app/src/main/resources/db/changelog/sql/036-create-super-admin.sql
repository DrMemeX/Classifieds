WITH super_admin AS (
INSERT INTO users (
    login,
    password,
    role,
    status,
    created_at
)
VALUES (
    'andrei-super-admin',
    '$2a$10$ACvqll01kJIN0u/P9TwD4Om14xuOa2GpBRF3p.Gj2pnRMQW2njvpC',
    'SUPER_ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP
    )
    RETURNING id
    )
INSERT INTO user_profiles (
    id,
    first_name,
    last_name,
    phone
)
SELECT
    id,
    'Super',
    'Admin',
    '+70000000000'
FROM super_admin;