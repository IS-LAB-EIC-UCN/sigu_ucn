INSERT INTO roles (nombre) VALUES
                               ('ADMIN'),
                               ('DOCENTE'),
                               ('ESTUDIANTE');

INSERT INTO usuarios (nombre, correo, password, activo, rol_id) VALUES
                                                                    ('Administrador General', 'admin@sigu.cl', 'pbkdf2-sha256$600000$YvDeIMU0ym+63BbhHcX9CQ==$EaZUcTNsM8wOjeJxy4E6BldTk/rgW6iBtFfhJpP+/QQ=', TRUE, 1),
                                                                    ('Docente Demo', 'docente@sigu.cl', 'pbkdf2-sha256$600000$EbHZOo7e3XwQqifdjXieSg==$nuIuWRzJHTfTO4AuYF9uqG5S9sZ5nhM133D9i0umSMg=', TRUE, 2),
                                                                    ('Estudiante Demo', 'estudiante@sigu.cl', 'pbkdf2-sha256$600000$pKy+cNI+dByFk7RuIPtB2A==$eMq0/rfdBzc/+fQF6zVzKk3XTNDgIE+5pRV+pVL3qHw=', TRUE, 3);

INSERT INTO espacios (nombre, tipo, capacidad, disponible) VALUES
                                                               ('Sala A101', 'SALA_CLASES', 40, TRUE),
                                                               ('Sala B202', 'SALA_REUNIONES', 20, TRUE),
                                                               ('Laboratorio L1', 'LABORATORIO', 25, TRUE),
                                                               ('Auditorio Central', 'AUDITORIO', 120, TRUE);

INSERT INTO reservas (fecha_reserva, hora_inicio, hora_fin, estado, usuario_id, espacio_id) VALUES
                                                                                                ('2026-04-15', '09:00:00', '11:00:00', 'APROBADA', 2, 1),
                                                                                                ('2026-04-16', '14:00:00', '15:30:00', 'PENDIENTE', 3, 2),
                                                                                                ('2026-04-17', '10:00:00', '12:00:00', 'CANCELADA', 3, 3);