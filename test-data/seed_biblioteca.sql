-- Seed de prueba para modulo biblioteca
-- Ejecutar despues de aplicar V001-V005

-- Lectores (RUTs validos verificados con modulo 11 chileno)
INSERT INTO lector (nombre, correo, rut, bloqueado) VALUES
('Gael Ortega', 'gael.ortega@ucn.cl', '20123456-5', false),
('Marianela Diaz', 'marianela.diaz@ucn.cl', '18234567-9', false),
('Matias Vidal', 'matias.vidal@ucn.cl', '19345678-2', false);

-- Libros
INSERT INTO libro (titulo, autor, categoria, isbn) VALUES
('Estructuras de Datos y Algoritmos', 'Alfred V. Aho', 'Programacion', '978-0-201-00023-8'),
('Introduccion a la Programacion', 'Luis Joyanes', 'Programacion', '978-8-473-29145-6'),
('Calculo Diferencial e Integral', 'James Stewart', 'Matematicas', '978-6-075-26722-0');

-- Ejemplares (3 del libro 1, 2 del libro 2, 2 del libro 3)
INSERT INTO ejemplar (estado, libro_id) VALUES
('DISPONIBLE', 1),
('DISPONIBLE', 1),
('DISPONIBLE', 1),
('DISPONIBLE', 2),
('DISPONIBLE', 2),
('DISPONIBLE', 3),
('DISPONIBLE', 3);
