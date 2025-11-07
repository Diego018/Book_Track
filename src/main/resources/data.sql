-- Roles
INSERT INTO rol (nombre) VALUES ('Admin');
INSERT INTO rol (nombre) VALUES ('Usuario');

-- Usuarios
INSERT INTO usuario (nombre, email, contraseña, id_rol)
VALUES ('Sebastian', 'sebas@example.com', '1234', 1);

INSERT INTO usuario (nombre, email, contraseña, id_rol)
VALUES ('Carlos', 'carlos@example.com', 'abcd', 2);

-- Libros
INSERT INTO libro (titulo, autor, genero, cantidad_disponible)
VALUES ('El Señor de los Anillos', 'Tolkien', 'Fantasía', 5);

INSERT INTO libro (titulo, autor, genero, cantidad_disponible)
VALUES ('1984', 'George Orwell', 'Distopía', 3);

INSERT INTO libro (titulo, autor, genero, cantidad_disponible)
VALUES ('Cien Años de Soledad', 'Gabriel García Márquez', 'Realismo Mágico', 4);
