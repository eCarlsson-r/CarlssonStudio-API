CREATE TABLE admin_users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    role       ENUM('SUPER_ADMIN', 'ADMIN') NOT NULL
               DEFAULT 'ADMIN',
    is_active  TINYINT(1) NOT NULL DEFAULT 1,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
               ON UPDATE CURRENT_TIMESTAMP
);

-- Default super admin (password: Admin@123 — change immediately)
INSERT INTO admin_users
    (username, password, email, role, is_active)
VALUES (
    'superadmin',
    '$2a$12$pCg2qMdvitSdy8aO8p7nNeqMNGKIlBllW5cYStUvdJPJB3uXU8aGS',
    'admin@carlssonstudio.com',
    'SUPER_ADMIN',
    1
);