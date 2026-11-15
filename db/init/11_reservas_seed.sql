-- =====================================================================
-- Grupo 1 - Datos de demostración del módulo de reservas
-- Las fechas son relativas a CURRENT_DATE para que siempre existan
-- reservas futuras al levantar la base (útil para la demo).
-- =====================================================================

UPDATE espacios SET ubicacion = 'Edificio A, piso 1'      WHERE nombre = 'Sala A101';
UPDATE espacios SET ubicacion = 'Edificio B, piso 2'      WHERE nombre = 'Sala B202';
UPDATE espacios SET ubicacion = 'Edificio de Laboratorios' WHERE nombre = 'Laboratorio L1';
UPDATE espacios SET ubicacion = 'Edificio Central'         WHERE nombre = 'Auditorio Central';

-- Motivo para las reservas históricas de 02_seed.sql
UPDATE reservas SET motivo = 'Reserva de ejemplo (datos base)' WHERE motivo IS NULL;

-- Reservas futuras para probar disponibilidad, aprobación y cancelación.
-- usuario_id: 1 = admin, 2 = docente, 3 = estudiante (ver 02_seed.sql)
-- espacio_id: 1 = A101, 2 = B202, 3 = L1, 4 = Auditorio
INSERT INTO reservas (fecha_reserva, hora_inicio, hora_fin, estado, usuario_id, espacio_id, motivo, motivo_rechazo) VALUES
    (CURRENT_DATE + 2, '09:00', '10:30', 'PENDIENTE', 3, 1, 'Estudio grupal Cálculo II',        NULL),
    (CURRENT_DATE + 2, '11:00', '12:30', 'APROBADA',  2, 1, 'Clase de reforzamiento',           NULL),
    (CURRENT_DATE + 3, '15:00', '17:00', 'PENDIENTE', 2, 3, 'Laboratorio de Redes',             NULL),
    (CURRENT_DATE + 4, '10:00', '11:00', 'RECHAZADA', 3, 2, 'Reunión de centro de alumnos',     'Sala reservada para consejo de escuela'),
    (CURRENT_DATE + 5, '14:00', '16:00', 'CANCELADA', 3, 4, 'Ensayo de presentación de tesis',  NULL);
