INSERT INTO users (id, username, password, role) VALUES (100, 'joey@gmail.com', '$2a$10$nyH2JBVUPa2DRAwWkW3RpuoQQ30c9H7NJkspf6YFr/qMlYkiChhe.', 'ADMIN');
INSERT INTO users (id, username, password, role) VALUES (101, 'tea@gmail.com', '$2a$10$nyH2JBVUPa2DRAwWkW3RpuoQQ30c9H7NJkspf6YFr/qMlYkiChhe.', 'CLIENT');
INSERT INTO users (id, username, password, role) VALUES (102, 'tristan@gmail.com', '$2a$10$nyH2JBVUPa2DRAwWkW3RpuoQQ30c9H7NJkspf6YFr/qMlYkiChhe.', 'CLIENT');
INSERT INTO users (id, username, password, role) VALUES (103, 'yugi@gmail.com', '$2a$10$nyH2JBVUPa2DRAwWkW3RpuoQQ30c9H7NJkspf6YFr/qMlYkiChhe.', 'CLIENT');

INSERT INTO clients (id, name, cpf, id_user) VALUES (10, 'Tea Gardner', '07141873058', 101);
INSERT INTO clients (id, name, cpf, id_user) VALUES (20, 'Tristan Taylor', '94636819063', 102);