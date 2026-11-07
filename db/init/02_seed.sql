-- Active: 1793928731847@@localhost@5432@sigu_ucn@public
INSERT INTO roles (nombre) VALUES
                                ('ADMIN'),
                                ('DOCENTE'),
                                ('ESTUDIANTE');

INSERT INTO usuarios (nombre, correo, password, activo, rol_id) VALUES
                                                                    ('Administrador General', 'admin@sigu.cl', 'admin123', TRUE, 1),
                                                                    ('Docente Demo', 'docente@sigu.cl', 'docente123', TRUE, 2),
                                                                    ('Estudiante Demo', 'estudiante@sigu.cl', 'estudiante123', TRUE, 3);

INSERT INTO espacios (nombre, tipo, capacidad, disponible) VALUES
                                                               ('Sala A101', 'SALA_CLASES', 40, TRUE),
                                                               ('Sala B202', 'SALA_REUNIONES', 20, TRUE),
                                                               ('Laboratorio L1', 'LABORATORIO', 25, TRUE),
                                                               ('Auditorio Central', 'AUDITORIO', 120, TRUE);

INSERT INTO reservas (fecha_reserva, hora_inicio, hora_fin, estado, usuario_id, espacio_id) VALUES
                                                                                                ('2026-04-15', '09:00:00', '11:00:00', 'APROBADA', 2, 1),
                                                                                                ('2026-04-16', '14:00:00', '15:30:00', 'PENDIENTE', 3, 2),
                                                                                                ('2026-04-17', '10:00:00', '12:00:00', 'CANCELADA', 3, 3);

-- ==========================================================
-- Datos iniciales Módulo Biblioteca (Grupo 2)
-- Sincronizados con los usuarios base (RNF-01, RN-04, RN-10, RN-11)
-- ==========================================================

-- Bibliotecario correspondiente a admin@sigu.cl
INSERT INTO bibliotecarios (rut, nombre, correo) VALUES
    ('11.111.111-1', 'Administrador General', 'admin@sigu.cl');

-- Lectores correspondientes a docente@sigu.cl y estudiante@sigu.cl
INSERT INTO lectores (rut, nombre, correo, tipo, estado) VALUES
    ('22.222.222-2', 'Docente Demo', 'docente@sigu.cl', 'DOCENTE', 'HABILITADO'),
    ('33.333.333-3', 'Estudiante Demo', 'estudiante@sigu.cl', 'ESTUDIANTE', 'HABILITADO');

-- Categorías
INSERT INTO categorias (nombre) VALUES
    ('Ingeniería de Software'),
    ('Bases de Datos'),
    ('Algoritmos y Estructuras de Datos');

-- Autores
INSERT INTO autores (nombre) VALUES
    ('Robert C. Martin'),
    ('Martin Fowler'),
    ('Abraham Silberschatz');

-- Libros (RN-11: al menos un autor y una categoría)
INSERT INTO libros (isbn, titulo, editorial, anio_publicacion, categoria_id) VALUES
    ('978-0132350884', 'Clean Code', 'Prentice Hall', 2008, 1),
    ('978-0201485677', 'Refactoring', 'Addison-Wesley', 1999, 1),
    ('978-0073523323', 'Database System Concepts', 'McGraw-Hill', 2010, 2);

-- Relación Libros - Autores
INSERT INTO libros_autores (libro_id, autor_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3);

-- Ejemplares (RN-01, RN-09, RN-10)
INSERT INTO ejemplares (codigo_inventario, ubicacion, estado, libro_id) VALUES
    ('EJ-CC-001', 'Estante A1', 'DISPONIBLE', 1),
    ('EJ-CC-002', 'Estante A1', 'DISPONIBLE', 1),
    ('EJ-RF-001', 'Estante A2', 'DISPONIBLE', 2),
    ('EJ-DB-001', 'Estante B1', 'DISPONIBLE', 3);
