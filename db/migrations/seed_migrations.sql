-- 1. Insertar asignaturas de prueba para el módulo
INSERT INTO asignaturas (codigo, nombre) VALUES
                                             ('INF-200', 'Estructuras de Datos'),
                                             ('INF-210', 'Programación Orientada a Objetos'),
                                             ('MAT-110', 'Cálculo I');

INSERT INTO roles (nombre) VALUES ('TUTOR');

INSERT INTO usuarios (nombre, correo, password, activo, rol_id) VALUES
    ('Estudiante Tutor Demo', 'tutor@sigu.cl', 'tutor123', TRUE, 4);

-- 2. Publicar una disponibilidad de prueba (Tutor Demo ofrece tutoría de POO)
INSERT INTO disponibilidades_tutores (tutor_id, asignatura_id, fecha, hora_inicio, hora_fin, estado) VALUES
    (4, 2, '2026-06-15', '10:00:00', '11:30:00', 'DISPONIBLE');