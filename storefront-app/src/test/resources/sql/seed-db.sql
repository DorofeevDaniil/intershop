INSERT INTO item (count, description, img_path, price, title, id) VALUES (0, 'test description 1', 'test img path 1', 100, 'test title 1', default);
INSERT INTO item (count, description, img_path, price, title, id) VALUES (0, 'test description 2', 'test img path 2', 200, 'test title 2', default);

INSERT INTO orders (id) VALUES (default);

INSERT INTO order_item (order_id, item_id, count, price, id) VALUES (1, 1, 1, 100, default);
INSERT INTO order_item (order_id, item_id, count, price, id) VALUES (1, 2, 1, 200, default);