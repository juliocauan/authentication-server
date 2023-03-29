CREATE SCHEMA auth;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE auth.users
(
    id uuid DEFAULT uuid_generate_v4 () PRIMARY KEY,
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
    user_id uuid REFERENCES auth.users(id),
    role_id SMALLINT REFERENCES auth.roles(id),
    PRIMARY KEY(user_id, role_id)
);

INSERT INTO auth.roles(name) VALUES
    ('ADMIN'),
    ('MANAGER'),
    ('USER');