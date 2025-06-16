CREATE TABLE CATEGORY (
    categoryId INT PRIMARY KEY AUTO_INCREMENT,
    categoria VARCHAR(100),
    estado BOOLEAN
);

CREATE TABLE PRODUCT (
    id_producto INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(100),
    descripcion VARCHAR(255),
    precio DECIMAL(16, 2),
    cantidad INT,
    categoryId INT,
    calificacion DOUBLE,
    total_reviews INT,
    state BOOLEAN,
    FOREIGN KEY (categoryId) REFERENCES CATEGORY(categoryId)
);

CREATE TABLE PURCHASE (
    id_compra INT PRIMARY KEY AUTO_INCREMENT,
    id_cliente VARCHAR(100),
    fecha TIMESTAMP,
    medio_pago VARCHAR(50),
    comentario VARCHAR(255),
    estado VARCHAR(50)
);

CREATE TABLE PURCHASE_ITEM (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_compra INT,
    id_producto INT,
    cantidad INT,
    total DECIMAL(16, 2),
    estado BOOLEAN,
    FOREIGN KEY (id_compra) REFERENCES PURCHASE(id_compra),
    FOREIGN KEY (id_producto) REFERENCES PRODUCT(id_producto)
);


-- Insert categories
INSERT INTO CATEGORY (categoria, estado) VALUES ('Electrónica', true);
INSERT INTO CATEGORY (categoria, estado) VALUES ('Hogar y Cocina', true);
INSERT INTO CATEGORY (categoria, estado) VALUES ('Ropa y Accesorios', true);

-- Insert products
INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Smartphone Samsung A54', 'Celular gama media con cámara cuádruple', 1349000.00, 50, 1, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Laptop Lenovo Ideapad 3', 'Portátil 15 Ryzen 5, 8GB RAM', 2099000.00, 20, 1, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Smart TV LG 55"', 'Televisor 4K UHD con webOS', 2890000.00, 15, 1, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Auriculares Sony WH-1000XM4', 'Audífonos inalámbricos con cancelación de ruido', 1299000.00, 40, 1, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Refrigerador Mabe', 'Refrigerador No Frost 300L', 1799000.00, 10, 2, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Licuadora Oster', 'Licuadora de vidrio con 3 velocidades', 219000.00, 35, 2, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Sartén Antiadherente 24cm', 'Sartén Tefal de aluminio', 89000.00, 60, 2, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Batidora de Mano Black+Decker', 'Batidora con accesorios incluidos', 149000.00, 25, 2, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Camiseta Polo Hombre', 'Camiseta tipo polo 100% algodón', 59000.00, 70, 3, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Jean Mujer Tiro Alto', 'Jean stretch azul oscuro', 109000.00, 50, 3, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Tenis Deportivos Adidas', 'Zapatillas running para hombre', 279000.00, 30, 3, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Chaqueta Impermeable Unisex', 'Chaqueta liviana para lluvia', 159000.00, 40, 3, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Cafetera Oster', 'Cafetera 12 tazas con filtro permanente', 169000.00, 20, 2, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Freidora de Aire 4L', 'Freidora sin aceite, digital', 349000.00, 18, 2, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Tablet Samsung Galaxy Tab A8', 'Pantalla 10.5, 64GB', 899000.00, 25, 1, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Monitor Dell 24"', 'Monitor FHD IPS con bordes delgados', 749000.00, 22, 1, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Teclado Mecánico Redragon', 'Retroiluminado RGB, switches rojos', 229000.00, 30, 1, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Vestido Casual Mujer', 'Vestido floral manga corta', 99000.00, 45, 3, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Pantaloneta Hombre', 'Pantaloneta deportiva secado rápido', 59000.00, 60, 3, 4.3, 88, TRUE);

INSERT INTO PRODUCT (titulo, descripcion, precio, cantidad, categoryId, calificacion, total_reviews, state)
VALUES ('Sandalias Playa Mujer', 'Sandalias cómodas con correa', 49000.00, 55, 3, 4.3, 88, TRUE);



-- Insert compras
INSERT INTO PURCHASE (id_cliente, fecha, medio_pago, comentario, estado) VALUES
('cliente01', CURRENT_TIMESTAMP, 'Tarjeta de crédito', 'Compra electrónica', 'APPROVED'),
('cliente02', CURRENT_TIMESTAMP, 'Efectivo', 'Compra hogar', 'PENDING'),
('cliente03', CURRENT_TIMESTAMP, 'Nequi', 'Compra ropa', 'CANCELED');

-- Insert items de compra con UUIDs generados
INSERT INTO PURCHASE_ITEM (id_compra, id_producto, cantidad, total, estado) VALUES
( 1, 1, 2, 199.98, true),
( 1, 2, 1, 149.99, true),
( 2, 2, 3, 449.97, true),
( 2, 3, 1, 79.99, true),
( 3, 1, 1, 99.99, false),
( 3, 3, 2, 159.98, false);