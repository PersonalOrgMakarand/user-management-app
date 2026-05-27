-- Seed data loaded by Spring Boot on startup.
-- Idempotent inserts so repeated runs do not fail.
MERGE INTO users (id, name, email, password, role) KEY(id) VALUES (1, 'Alice Admin', 'alice@example.com', 'secret1', 'ADMIN');
MERGE INTO users (id, name, email, password, role) KEY(id) VALUES (2, 'Bob User',   'bob@example.com',   'secret1', 'USER');
MERGE INTO users (id, name, email, password, role) KEY(id) VALUES (3, 'Carol User', 'carol@example.com', 'secret1', 'USER');
MERGE INTO users (id, name, email, password, role) KEY(id) VALUES (4, 'Test User', 'test@example.com', 'secret1', 'USER');