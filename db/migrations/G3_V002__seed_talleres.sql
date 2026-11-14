-- =====================================================================
-- Grupo 3 - Datos de prueba para el módulo de talleres
-- Las fechas son relativas a CURRENT_DATE para que siempre sean futuras.
-- =====================================================================

INSERT INTO usuarios (nombre, correo, password, activo, rol_id) VALUES
    ('Participante Uno',  'g3.participante1@sigu.cl', 'part123', TRUE, 3),
    ('Participante Dos',  'g3.participante2@sigu.cl', 'part123', TRUE, 3);

INSERT INTO talleres (nombre, descripcion, categoria, fecha, hora_inicio, hora_fin, lugar, cupo_maximo) VALUES
    ('Introducción a Git y GitHub', 'Control de versiones para proyectos colaborativos.', 'TECNOLOGIA', CURRENT_DATE + 7,  '10:00', '12:00', 'Laboratorio L1', 20),
    ('Oratoria y presentaciones',   'Técnicas para exponer con seguridad.',              'HABILIDADES_BLANDAS', CURRENT_DATE + 7,  '11:00', '13:00', 'Sala B202', 2),
    ('Fotografía básica',           'Composición, luz y edición simple.',                'ARTE',       CURRENT_DATE + 10, '15:00', '17:00', 'Sala A101', 15),
    ('Yoga para estudiantes',       'Manejo del estrés y postura.',                      'DEPORTE',    CURRENT_DATE + 14, '09:00', '10:30', 'Auditorio Central', 30),
    ('Excel para ingeniería',       'Fórmulas, tablas dinámicas y gráficos.',            'TECNOLOGIA', CURRENT_DATE + 21, '14:00', '16:00', 'Laboratorio L1', 25);

-- Taller "Oratoria" (cupo 2) queda lleno para demostrar la lista de espera.
INSERT INTO inscripciones (taller_id, usuario_id, estado, fecha_inscripcion)
SELECT t.id, u.id, 'INSCRITO', CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM talleres t, usuarios u
WHERE t.nombre = 'Oratoria y presentaciones'
  AND u.correo IN ('g3.participante1@sigu.cl', 'g3.participante2@sigu.cl');
