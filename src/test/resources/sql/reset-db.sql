DELETE FROM order_item;
DELETE FROM orders;
DELETE FROM item;

ALTER TABLE order_item ALTER COLUMN id RESTART WITH 1;
ALTER TABLE orders ALTER COLUMN id RESTART WITH 1;
ALTER TABLE item ALTER COLUMN id RESTART WITH 1;