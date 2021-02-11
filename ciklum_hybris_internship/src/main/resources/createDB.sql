create database if not exists orderdb;
use orderdb;

CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_at` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `price` int DEFAULT NULL,
  `status` enum('OUT_OF_STOCK','IN_STOCK','RUNNING_LOW') DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `order_item` (
  `order_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
);

insert into products(name, price, status, created_at) 
value('chocolate', 23, 'in_stock', '28.01.2021'),
('tea', 15, 'in_stock', '28.01.2021'),
('coffee', 15, 'in_stock', '28.01.2021'),
('juce', 18.90, 'in_stock', '28.01.2021'),
('pizza', 60, 'in_stock', '28.01.2021'),
('mask', 8, 'in_stock', '28.01.2021'),
('meat', 120, 'in_stock', '28.01.2021');

insert into orders(user_id, status, created_at)
value(85624, 'processed', '28/01/2021 15:30');

insert into order_item(order_id, product_id, quantity)
value(1, 5, 3),
(1, 3, 6),
(1, 6, 54);