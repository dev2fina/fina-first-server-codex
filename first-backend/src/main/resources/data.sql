INSERT INTO roles (id, name, description, created_at, updated_at)
VALUES (1, 'ADMIN', 'Default administrator role', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (id, name, description, created_at, updated_at)
VALUES (2, 'EDITOR', 'Registration editor', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (id, name, description, created_at, updated_at)
VALUES (3, 'CONTROLLER', 'Controller reviewer', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password_hash, email, active, created_at, updated_at)
VALUES (1, 'admin', '$2a$10$Dow1xAEZXQ6oBOkDw9LHCujLMrHbKO3E6sbZteG3Rowkb5xDgnqUy', 'admin@example.com', true, now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;
