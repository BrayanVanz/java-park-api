INSERT INTO users (id, username, password, role) VALUES (100, 'joey@gmail.com', '$2a$10$nyH2JBVUPa2DRAwWkW3RpuoQQ30c9H7NJkspf6YFr/qMlYkiChhe.', 'ADMIN');
INSERT INTO users (id, username, password, role) VALUES (101, 'tea@gmail.com', '$2a$10$nyH2JBVUPa2DRAwWkW3RpuoQQ30c9H7NJkspf6YFr/qMlYkiChhe.', 'CLIENT');
INSERT INTO users (id, username, password, role) VALUES (102, 'tristan@gmail.com', '$2a$10$nyH2JBVUPa2DRAwWkW3RpuoQQ30c9H7NJkspf6YFr/qMlYkiChhe.', 'CLIENT');

INSERT INTO clients (id, name, cpf, id_user) VALUES (21, 'Tea Gardner', '07141873058', 101);
INSERT INTO clients (id, name, cpf, id_user) VALUES (22, 'Tristan Taylor', '94636819063', 102);

INSERT INTO parking_spaces (id, code, status) VALUES (100, 'A-01', 'TAKEN');
INSERT INTO parking_spaces (id, code, status) VALUES (200, 'A-02', 'TAKEN');
INSERT INTO parking_spaces (id, code, status) VALUES (300, 'A-03', 'TAKEN');
INSERT INTO parking_spaces (id, code, status) VALUES (400, 'A-04', 'AVAILABLE');
INSERT INTO parking_spaces (id, code, status) VALUES (500, 'A-05', 'AVAILABLE');

INSERT INTO client_parking_spaces (receipt, plate, brand, model, color, entry_date, id_client, id_parking_space)
    VALUES ('20250313-101300', 'FIT-1020', 'FIAT', 'PALIO', 'GREEN', '2025-03-13 10:15:00', 22, 100);
INSERT INTO client_parking_spaces (receipt, plate, brand, model, color, entry_date, id_client, id_parking_space)
    VALUES ('20250314-101400', 'SIE-1020', 'FIAT', 'SIENA', 'WHITE', '2025-03-14 10:15:00', 21, 200);
INSERT INTO client_parking_spaces (receipt, plate, brand, model, color, entry_date, id_client, id_parking_space)
    VALUES ('20250315-101500', 'FIT-1020', 'FIAT', 'PALIO', 'GREEN', '2025-03-14 10:15:00', 22, 300);