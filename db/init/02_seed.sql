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
INSERT INTO recursos (nombre, stock, tipo) VALUES
                                               ('Sal', 15, 'INSUMO'),
                                               ('Cobre-10g', 10, 'INSUMO'),
                                               ('Dell-Desktop', 8, 'EQUIPO'),
                                               ('Medidor-Ph', 6, 'EQUIPO');
INSERT INTO proveedores (nombre, correo, telefono) VALUES
                                                        ('Proveedor 1', 'prov1@sigu.cl', '+56910001000'),
                                                        ('Proveedor 2', 'prov2@sigu.cl', '+56920002000');
INSERT INTO movimientosInventario (recurso_id, cantidad, fecha, hora) VALUES
                                                                          (1, 3, '2026-04-15', '10:00:00'),
                                                                          (2, 2, '2026-04-16', '15:00:00'),
                                                                          (1, 4, '2026-04-17', '09:00:00'),
                                                                          (2, 2, '2026-04-18', '09:30:00'),
                                                                          (3, 1, '2026-04-19', '15:30:00'),
                                                                          (4, 1, '2026-04-20', '11:30:00');
INSERT INTO entradas (movimiento_id, proveedor_id) VALUES
                                                       (1, 1),
                                                       (2, 2);
INSERT INTO salidas (movimiento_id) VALUES
                                        (3),
                                        (4);
INSERT INTO prestamos (movimiento_id, usuario_id, estado) VALUES
                                                              (5, 2, 'PRESTAMO PENDIENTE'),
                                                              (6, 2, 'DEVOLUCION PENDIENTE');