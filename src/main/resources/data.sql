-- Roles
INSERT INTO rol (desc_rol) VALUES ('Admin');
INSERT INTO rol (desc_rol) VALUES ('Usuario');

-- Géneros de Libros
INSERT INTO genero_libro (desc_libro) VALUES ('Fantasía');
INSERT INTO genero_libro (desc_libro) VALUES ('Distopía');
INSERT INTO genero_libro (desc_libro) VALUES ('Realismo Mágico');
INSERT INTO genero_libro (desc_libro) VALUES ('Misterio');
INSERT INTO genero_libro (desc_libro) VALUES ('Ciencia Ficción');
INSERT INTO genero_libro (desc_libro) VALUES ('Romance');
INSERT INTO genero_libro (desc_libro) VALUES ('Drama');
INSERT INTO genero_libro (desc_libro) VALUES ('Aventura');

-- Usuarios
INSERT INTO usuario (nombre, email, contraseña, id_rol)
VALUES ('Sebastian', 'sebas@example.com', '1234', 1);

INSERT INTO usuario (nombre, email, contraseña, id_rol)
VALUES ('Carlos', 'carlos@example.com', 'abcd', 2);

INSERT INTO usuario (nombre, email, contraseña, id_rol)
VALUES ('María', 'maria@example.com', 'pass123', 2);

INSERT INTO usuario (nombre, email, contraseña, id_rol)
VALUES ('Juan', 'juan@example.com', 'pass456', 2);

-- Libros
INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('El Señor de los Anillos', 'J.R.R. Tolkien', '1954-07-29', 5, 5, 1);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('1984', 'George Orwell', '1949-06-08', 3, 3, 2);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('Cien Años de Soledad', 'Gabriel García Márquez', '1967-05-30', 4, 4, 3);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('El Hobbit', 'J.R.R. Tolkien', '1937-09-21', 6, 6, 1);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('Dune', 'Frank Herbert', '1965-06-01', 4, 4, 5);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('Orgullo y Prejuicio', 'Jane Austen', '1813-01-28', 5, 5, 6);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('El Código Da Vinci', 'Dan Brown', '2003-03-18', 7, 7, 4);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('La Isla del Tesoro', 'Robert Louis Stevenson', '1881-11-14', 3, 3, 8);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('Fahrenheit 451', 'Ray Bradbury', '1953-10-19', 4, 4, 2);

INSERT INTO libro (titulo, autor, fecha, cantidad_total, cantidad_disponible, id_genero_libro)
VALUES ('El Gran Gatsby', 'F. Scott Fitzgerald', '1925-04-10', 5, 5, 7);
