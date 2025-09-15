INSERT INTO roles (name) VALUES('USER');
INSERT INTO roles (name) VALUES('ADMIN');

INSERT INTO users (username, password, enabled) VALUES('user', '$2a$10$I.tTxrGb6GcD5SvZEAxl.OXTXBVamr.N7jJ30BjhEnMYtCoMlopym', true);
INSERT INTO users (username, password, enabled) VALUES('admin', '$2a$10$5rwSSZoxpQ.WKPA/vpge0efs2pjlb.Zo8ZTesozlIaIpYAT3.en1G', true);

INSERT INTO user_role (user_id, role_id) VALUES(1, 1);
INSERT INTO user_role (user_id, role_id) VALUES(2, 2);

INSERT INTO item (count, description, img_path, price, title, id) VALUES (0, 'test description 1', 'test img path 1', 100, 'test title 1', default);
INSERT INTO item (count, description, img_path, price, title, id) VALUES (0, 'test description 2', 'test img path 2', 200, 'test title 2', default);

INSERT INTO orders (id, user_id) VALUES (default, 1);

INSERT INTO order_item (order_id, item_id, count, price, id) VALUES (1, 1, 1, 100, default);
INSERT INTO order_item (order_id, item_id, count, price, id) VALUES (1, 2, 1, 200, default);