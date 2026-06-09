INSERT INTO roles (nombre) VALUES
('ADMIN'),
('DOCENTE'),
('ESTUDIANTE');

INSERT INTO usuarios (nombre, correo, password, activo, rol_id) VALUES
('Administrador General', 'admin@sigu.cl', 'admin123', TRUE, 1),
('Docente Demo', 'docente@sigu.cl', 'docente123', TRUE, 2),
('Estudiante Demo', 'estudiante@sigu.cl', 'estudiante123', TRUE, 3),
('Ana Lopez', 'ana.lopez@alumnos.ucn.cl', 'ana123', TRUE, 3),
('Carlos Perez', 'carlos.perez@alumnos.ucn.cl', 'carlos123', TRUE, 3);

INSERT INTO espacios (nombre, tipo, capacidad, disponible) VALUES
('Sala A101', 'SALA_CLASES', 40, TRUE),
('Sala B202', 'SALA_REUNIONES', 20, TRUE),
('Laboratorio L1', 'LABORATORIO', 25, TRUE),
('Auditorio Central', 'AUDITORIO', 120, TRUE);

INSERT INTO reservas (fecha_reserva, hora_inicio, hora_fin, estado, usuario_id, espacio_id) VALUES
('2026-04-15', '09:00:00', '11:00:00', 'APROBADA', 2, 1),
('2026-04-16', '14:00:00', '15:30:00', 'PENDIENTE', 3, 2),
('2026-04-17', '10:00:00', '12:00:00', 'CANCELADA', 3, 3);

INSERT INTO talleres (nombre, descripcion, categoria, cupos_totales, fecha_inicio, fecha_fin, estado, bloque_horario, profesor_id, espacio_id) VALUES
('Taller de Robotica Basica', 'Introduccion a la electronica y programacion con Arduino.', 'Tecnología', 25, '2026-05-01', '2026-07-01', 'ABIERTO', 'C', 2, 3),
('Taller de Liderazgo Estudiantil', 'Desarrollo de habilidades blandas, oratoria y trabajo en equipo.', 'Desarrollo Personal', 40, '2026-05-15', '2026-06-15', 'ABIERTO', 'E', 2, 4),
('Taller de Fotografia (1 Cupo)', 'Taller disenado para probar la lista de espera.', 'Arte', 1, '2026-06-01', '2026-06-30', 'ABIERTO', 'A', 2, 1);

INSERT INTO inscripciones (taller_id, usuario_id, estado) VALUES
(1, 3, 'INSCRITO');