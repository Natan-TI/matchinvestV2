CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tabelas de autenticação e autorização

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Inserção inicial de roles
INSERT INTO roles (id, name)
VALUES 
    (gen_random_uuid(), 'ROLE_ADMIN'),
    (gen_random_uuid(), 'ROLE_USER');

-- Usuário admin inicial (senha: 'admin123')
INSERT INTO users (id, name, email, password)
VALUES (
    gen_random_uuid(),
    'Administrador',
    'admin@matchinvest.com',
    '$2b$12$gT8U7mPIU6MRc8fwjWw1c.FLY79GkuuMD1cpg4ivUKsH0hZJ.4puK'
);

-- Associação do admin à role admin
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@matchinvest.com' AND r.name = 'ROLE_ADMIN';
