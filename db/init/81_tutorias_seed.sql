-- Grupo 8 (Tutorías) - Datos mock

INSERT INTO usuarios (nombre, correo, password, activo, rol_id)
SELECT v.nombre, v.correo, v.password, TRUE, r.id
FROM (VALUES
    ('Ana Rojas',     'ana.rojas@alumnos.ucn.cl',     'tutor123'),
    ('Tomás Pizarro', 'tomas.pizarro@alumnos.ucn.cl', 'tutor123'),
    ('Bruno Castro',  'bruno.castro@alumnos.ucn.cl',  'estudiante123'),
    ('Carla Muñoz',   'carla.munoz@alumnos.ucn.cl',   'estudiante123')
) AS v(nombre, correo, password)
JOIN roles r ON r.nombre = 'ESTUDIANTE';

INSERT INTO tut_asignaturas (codigo, nombre, carrera, semestre) VALUES
    ('DCCB-101', 'Cálculo I',              'Ingeniería Civil en Computación e Informática', 1),
    ('DCCB-102', 'Álgebra I',              'Ingeniería Civil en Computación e Informática', 1),
    ('EIC-201', 'Programación',           'Ingeniería Civil en Computación e Informática', 1),
    ('EIC-401', 'Estructuras de Datos',   'Ingeniería Civil en Computación e Informática', 3),
    ('EIC-601', 'Ingeniería de Software', 'Ingeniería Civil en Computación e Informática', 5);

INSERT INTO tut_estudiantes (correo, nombre, carrera, semestre, usuario_id)
SELECT v.correo, v.nombre, 'Ingeniería Civil en Computación e Informática', v.semestre, u.id
FROM (VALUES
    ('ana.rojas@alumnos.ucn.cl',     'Ana Rojas',     5),
    ('tomas.pizarro@alumnos.ucn.cl', 'Tomás Pizarro', 5),
    ('bruno.castro@alumnos.ucn.cl',  'Bruno Castro',  1),
    ('carla.munoz@alumnos.ucn.cl',   'Carla Muñoz',   1),
    ('diego.soto@alumnos.ucn.cl',    'Diego Soto',    1)
) AS v(correo, nombre, semestre)
LEFT JOIN usuarios u ON u.correo = v.correo;

INSERT INTO tut_estudiante_asignaturas (estudiante_id, asignatura_id)
SELECT e.id, a.id
FROM (VALUES
    ('ana.rojas@alumnos.ucn.cl',     'EIC-601'),
    ('tomas.pizarro@alumnos.ucn.cl', 'EIC-601'),
    ('bruno.castro@alumnos.ucn.cl',  'DCCB-101'),
    ('bruno.castro@alumnos.ucn.cl',  'EIC-201'),
    ('carla.munoz@alumnos.ucn.cl',   'DCCB-101'),
    ('carla.munoz@alumnos.ucn.cl',   'DCCB-102'),
    ('diego.soto@alumnos.ucn.cl',    'DCCB-101'),
    ('diego.soto@alumnos.ucn.cl',    'EIC-201')
) AS v(correo, codigo)
JOIN tut_estudiantes e ON e.correo = v.correo
JOIN tut_asignaturas a ON a.codigo = v.codigo;

INSERT INTO tut_tutores (estudiante_id, certificado)
SELECT id, TRUE
FROM tut_estudiantes
WHERE correo IN ('ana.rojas@alumnos.ucn.cl', 'tomas.pizarro@alumnos.ucn.cl');

INSERT INTO tut_tutor_asignaturas (tutor_id, asignatura_id)
SELECT t.id, a.id
FROM (VALUES
    ('ana.rojas@alumnos.ucn.cl',     'DCCB-101'),
    ('ana.rojas@alumnos.ucn.cl',     'DCCB-102'),
    ('tomas.pizarro@alumnos.ucn.cl', 'EIC-201')
) AS v(correo, codigo)
JOIN tut_estudiantes e ON e.correo = v.correo
JOIN tut_tutores t ON t.estudiante_id = e.id
JOIN tut_asignaturas a ON a.codigo = v.codigo;

INSERT INTO tut_tutorias (tutor_id, asignatura_id, inicio, fin, modalidad, sala, capacidad, estado)
SELECT t.id, a.id,
       CURRENT_DATE + v.dia + v.hora,
       CURRENT_DATE + v.dia + v.hora + v.duracion,
       v.modalidad, v.sala, v.capacidad, 'PUBLICADA'
FROM (VALUES
    ('ana.rojas@alumnos.ucn.cl',     'DCCB-101', INTERVAL '2 days', INTERVAL '10 hours',            INTERVAL '90 minutes', 'PRESENCIAL', 'Y-103', 10),
    ('tomas.pizarro@alumnos.ucn.cl', 'EIC-201', INTERVAL '2 days', INTERVAL '10 hours 30 minutes', INTERVAL '60 minutes', 'ONLINE',     NULL,    15),
    ('ana.rojas@alumnos.ucn.cl',     'DCCB-101', INTERVAL '3 days', INTERVAL '16 hours',            INTERVAL '60 minutes', 'PRESENCIAL', 'X-105', 1),
    ('ana.rojas@alumnos.ucn.cl',     'DCCB-102', INTERVAL '4 days', INTERVAL '18 hours',            INTERVAL '45 minutes', 'ONLINE',     NULL,    20)
) AS v(correo, codigo, dia, hora, duracion, modalidad, sala, capacidad)
JOIN tut_estudiantes e ON e.correo = v.correo
JOIN tut_tutores t ON t.estudiante_id = e.id
JOIN tut_asignaturas a ON a.codigo = v.codigo;

INSERT INTO tut_inscripciones (tutoria_id, estudiante_id, estado)
SELECT tu.id, e.id, 'RESERVADA'
FROM (VALUES
    ('bruno.castro@alumnos.ucn.cl', 'Y-103'),
    ('carla.munoz@alumnos.ucn.cl',  'X-105')
) AS v(correo, sala)
JOIN tut_estudiantes e ON e.correo = v.correo
JOIN tut_tutorias tu ON tu.sala = v.sala;
