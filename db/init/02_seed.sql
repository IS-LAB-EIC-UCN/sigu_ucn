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