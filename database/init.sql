CREATE SCHEMA auth;

CREATE TABLE auth.users
(
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL
);
CREATE TABLE auth.roles
(
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
CREATE TABLE auth.users_roles
(
    user_id INTEGER REFERENCES auth.users(id),
    role_id SMALLINT REFERENCES auth.roles(id),
    PRIMARY KEY(user_id, role_id)
);
CREATE TABLE auth.password_reset_tokens
(
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES auth.users(id) ON DELETE CASCADE UNIQUE NOT NULL,
    token VARCHAR(43) UNIQUE NOT NULL,
    expire_date timestamp NOT NULL
);

INSERT INTO auth.roles(name) VALUES
    ('ADMIN'),
    ('MANAGER'),
    ('USER');