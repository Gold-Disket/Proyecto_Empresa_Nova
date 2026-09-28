CREATE DATABASE NOVA_ONLINE;
GO

USE NOVA_ONLINE;
GO

------------------------ ALMACÉN 1 ------------------------

CREATE TABLE PRODUCTOS (
	Codigo VARCHAR(10) NOT NULL PRIMARY KEY, Nombre VARCHAR (100) NOT NULL,
	Precio DECIMAL (10,2) NOT NULL
);

CREATE TABLE INVENTARIO (
	Codigo VARCHAR(10) NOT NULL PRIMARY KEY REFERENCES PRODUCTOS(Codigo),
	Cantidad INT NOT NULL DEFAULT 0
);

------------------------ ALMACÉN 2 ------------------------

CREATE TABLE PROVEEDORES (
	Cedula_Juridica VARCHAR(10) NOT NULL PRIMARY KEY,   -- corregido: le faltaba PRIMARY KEY (COMPRAS la necesita para su FOREIGN KEY)
	Nombre VARCHAR(100) NOT NULL,			------- El de la empresa
	Nombre_Contacto VARCHAR(75) NOT NULL,
	Telefono VARCHAR(8) NOT NULL,
	Correo VARCHAR(80) NOT NULL,
	Estado CHAR(1) NOT NULL			------- "1" si está activo, y "2" si no lo está
);

------------------------ ALMACÉN 3 ------------------------

CREATE TABLE COMPRAS (
	Numero_Ingreso VARCHAR(10) NOT NULL PRIMARY KEY,
	Fecha_Compra DATE NOT NULL,
	Cedula_Juridica VARCHAR (10) NOT NULL REFERENCES PROVEEDORES(Cedula_Juridica)
);

CREATE TABLE Detalle_Compra (
	ID INT IDENTITY(1,1) PRIMARY KEY,
	Numero_Ingreso VARCHAR(10) NOT NULL REFERENCES COMPRAS(Numero_Ingreso),
	Codigo_Producto VARCHAR(10) NOT NULL REFERENCES PRODUCTOS(Codigo),
	Cantidad INT NOT NULL
);

------------------------ ALMACÉN 4 ------------------------

CREATE TABLE SALIDAS (
	Numero_Factura VARCHAR(10) NOT NULL PRIMARY KEY,
	Fecha_Venta DATE NOT NULL
);

CREATE TABLE DETALLE_SALIDA (
	ID INT IDENTITY(1,1) PRIMARY KEY,
	Numero_Factura VARCHAR(10) NOT NULL REFERENCES SALIDAS(Numero_Factura),
	Codigo_Producto VARCHAR(10) NOT NULL REFERENCES PRODUCTOS(Codigo),
	Cantidad INT NOT NULL
);


------------------------ DATOS DE PRUEBA ------------------------

------------------------ ALMACEN 1: Productos e Inventario ------------------------

INSERT INTO PRODUCTOS (Codigo, Nombre, Precio) VALUES
('3444550000', 'Camiseta con Estampado Floral de Multiples Colores', 17500.00),
('3444560000', 'Pantalon de Mezclilla Corte Recto',                  25990.00),
('3444570000', 'Zapatos Deportivos Talla 42',                        45000.00),
('3444580000', 'Gorra Ajustable Color Negro',                         8500.00),
('3444590000', 'Chaqueta Impermeable para Lluvia',                   39900.00);
GO

INSERT INTO INVENTARIO (Codigo, Cantidad) VALUES
('3444550000', 120),
('3444560000', 80),
('3444570000', 45),
('3444580000', 200),
('3444590000', 30);
GO

------------------------ ALMACEN 2

INSERT INTO PROVEEDORES (Cedula_Juridica, Nombre, Nombre_Contacto, Telefono, Correo, Estado) VALUES
('3100550000', 'Soluciones Empresariales Elite Jimenez y Hernandez S.A.', 'Javier Fonseca Alvarado',   '89403536', 'ventas@solucioneselite.cr',    '1'),
('3101560000', 'Textiles del Pacifico Sociedad Anonima',                  'Maria Rodriguez Chinchilla', '88123456', 'contacto@textilespacifico.cr', '1'),
('3102570000', 'Importadora Central S.A.',                                'Carlos Solano Vega',         '87654321', 'info@importadoracentral.cr',   '2'); -- Estado '2' = Inactivo, a proposito, para probar esa validacion
GO

------------------------ ALMACEN 3

INSERT INTO COMPRAS (Numero_Ingreso, Fecha_Compra, Cedula_Juridica) VALUES
('2526202000', '2025-08-30', '3100550000'),
('2526202001', '2025-09-05', '3101560000');
GO

INSERT INTO Detalle_Compra (Numero_Ingreso, Codigo_Producto, Cantidad) VALUES
('2526202000', '3444550000', 50),
('2526202000', '3444580000', 100),
('2526202001', '3444560000', 40),
('2526202001', '3444570000', 20);
GO

------------------------ ALMACEN 4

INSERT INTO SALIDAS (Numero_Factura, Fecha_Venta) VALUES
('4001000001', '2025-09-10'),
('4001000002', '2025-09-15');
GO

INSERT INTO DETALLE_SALIDA (Numero_Factura, Codigo_Producto, Cantidad) VALUES
('4001000001', '3444550000', 10),
('4001000001', '3444590000', 5),
('4001000002', '3444570000', 8);
GO

-- Esto es para verificar

SELECT * FROM PRODUCTOS;
SELECT * FROM INVENTARIO;
SELECT * FROM PROVEEDORES;
SELECT * FROM COMPRAS;
SELECT * FROM Detalle_Compra;
SELECT * FROM SALIDAS;
SELECT * FROM DETALLE_SALIDA;