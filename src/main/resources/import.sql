-- inserts a new ADMIN user to the table Users
INSERT INTO users (id, name, surname, email) VALUES (1, 'Admin', 'User', 'admin@siw.art');

--Password: admin234
INSERT INTO credentials (id, username, password, user_role, user_id) VALUES (1, 'admin', '$2a$10$3LRJZvJ9ZHh.NSGmBhHHOOaXMbYcLOhqukNNH0ABxoboLGHyp9D2C', 'ADMIN', 1);

-- other users: mario rossi/ federico pupolo

ALTER SEQUENCE users_seq RESTART WITH 100;
ALTER SEQUENCE credentials_seq RESTART WITH 100;
ALTER SEQUENCE artist_seq RESTART WITH 100;
ALTER SEQUENCE artwork_seq RESTART WITH 100;
ALTER SEQUENCE museum_seq RESTART WITH 100;
