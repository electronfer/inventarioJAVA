create database testdb;
use testdb;

CREATE TABLE Productos (
    codigo int AUTO_INCREMENT NOT NULL PRIMARY KEY,
    nombre varchar(255) NOT NULL,
    precio double NOT NULL,
    inventario int NOT NULL
);

insert into Productos (codigo, nombre, precio, inventario) values (1, "Manzanas", 5000.0, 25);
insert into Productos (codigo, nombre, precio, inventario) values (2, "Limones", 2300.0, 15);
insert into Productos (codigo, nombre, precio, inventario) values (3, "Peras", 2700.0, 33);
insert into Productos (codigo, nombre, precio, inventario) values (4, "Arandanos", 9300.0, 5);
insert into Productos (codigo, nombre, precio, inventario) values (5, "Tomates", 2100.0, 42);
insert into Productos (codigo, nombre, precio, inventario) values (6, "Fresas", 4100.0, 3);
insert into Productos (codigo, nombre, precio, inventario) values (7, "Helado", 4500.0, 41);
insert into Productos (codigo, nombre, precio, inventario) values (8, "Galletas", 500.0, 8);
insert into Productos (codigo, nombre, precio, inventario) values (9, "Chocolates", 3500.0, 80);
insert into Productos (codigo, nombre, precio, inventario) values (10, "Jamon", 15000.0, 10);

SELECT * FROM Productos;
#select sum(precio*inventario) as valor_inventario, sum(precio)/count(inventario) as promedio_precios from Productos;