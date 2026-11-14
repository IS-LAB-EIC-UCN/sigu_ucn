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
-- Grupo 5

INSERT INTO roles (nombre) VALUES ('ENCARGADO');

INSERT INTO usuarios (nombre, correo, password, activo, rol_id) VALUES
    ('Encargado Laboratorio', 'encargado@sigu.cl', 'encargado123', TRUE,
     (SELECT id FROM roles WHERE nombre = 'ENCARGADO'));

INSERT INTO inv_categorias (nombre) VALUES
    ('Microscopía'),
    ('Electrónica'),
    ('Computación'),
    ('Reactivos químicos'),
    ('Material de vidrio'),
    ('Elementos de protección');

INSERT INTO inv_proveedores (identificador, nombre, telefono, correo, direccion) VALUES
    ('76.123.456-7', 'LabSupply Chile SpA', '+56 51 222 3344', 'ventas@labsupply.cl', 'Av. del Mar 1200, La Serena'),
    ('77.987.654-3', 'ElectroTec Ltda.', '+56 51 233 4455', 'contacto@electrotec.cl', 'Calle Aldunate 845, Coquimbo');

-- Equipos (estado DISPONIBLE / PRESTADO)
INSERT INTO inv_productos (tipo, codigo, nombre, descripcion, activo, categoria_id, proveedor_id, estado) VALUES
    ('EQUIPO', 'EQ-001', 'Microscopio óptico binocular', 'Aumento 40x-1000x con iluminación LED', TRUE,
     (SELECT id FROM inv_categorias WHERE nombre = 'Microscopía'),
     (SELECT id FROM inv_proveedores WHERE identificador = '76.123.456-7'), 'DISPONIBLE'),
    ('EQUIPO', 'EQ-002', 'Osciloscopio digital', '2 canales, 100 MHz', TRUE,
     (SELECT id FROM inv_categorias WHERE nombre = 'Electrónica'),
     (SELECT id FROM inv_proveedores WHERE identificador = '77.987.654-3'), 'PRESTADO'),
    ('EQUIPO', 'EQ-003', 'Multímetro digital', 'Medición de voltaje, corriente y resistencia', TRUE,
     (SELECT id FROM inv_categorias WHERE nombre = 'Electrónica'),
     (SELECT id FROM inv_proveedores WHERE identificador = '77.987.654-3'), 'DISPONIBLE'),
    ('EQUIPO', 'EQ-004', 'Notebook de laboratorio', 'Intel i5, 16 GB RAM', TRUE,
     (SELECT id FROM inv_categorias WHERE nombre = 'Computación'),
     NULL, 'DISPONIBLE');

-- Insumos (stock_actual, stock_minimo, stock_inicializado)
INSERT INTO inv_productos (tipo, codigo, nombre, descripcion, activo, categoria_id, proveedor_id,
                           stock_actual, stock_minimo, stock_inicializado) VALUES
    ('INSUMO', 'INS-001', 'Guantes de nitrilo (caja 100 un.)', 'Talla M', TRUE,
     (SELECT id FROM inv_categorias WHERE nombre = 'Elementos de protección'),
     (SELECT id FROM inv_proveedores WHERE identificador = '76.123.456-7'), 20, 5, TRUE),
    ('INSUMO', 'INS-002', 'Alcohol etílico 70% (1 L)', 'Para limpieza de material', TRUE,
     (SELECT id FROM inv_categorias WHERE nombre = 'Reactivos químicos'),
     (SELECT id FROM inv_proveedores WHERE identificador = '76.123.456-7'), 3, 5, TRUE),
    ('INSUMO', 'INS-003', 'Tubo de ensayo 16x150 mm', 'Vidrio borosilicato', TRUE,
     (SELECT id FROM inv_categorias WHERE nombre = 'Material de vidrio'),
     (SELECT id FROM inv_proveedores WHERE identificador = '76.123.456-7'), 100, 30, TRUE),
    ('INSUMO', 'INS-004', 'Resistencia 220 ohm', 'Pack de resistencias 1/4 W', TRUE,
     (SELECT id FROM inv_categorias WHERE nombre = 'Electrónica'),
     (SELECT id FROM inv_proveedores WHERE identificador = '77.987.654-3'), 0, 50, FALSE);

-- Préstamo activo que justifica el estado PRESTADO de EQ-002,
-- y un préstamo ya finalizado de EQ-001 para el historial.
INSERT INTO inv_prestamos (equipo_id, usuario_id, fecha_prestamo, fecha_devolucion, estado) VALUES
    ((SELECT id FROM inv_productos WHERE codigo = 'EQ-002'),
     (SELECT id FROM usuarios WHERE correo = 'estudiante@sigu.cl'),
     '2026-11-10 10:00:00', NULL, 'ACTIVO'),
    ((SELECT id FROM inv_productos WHERE codigo = 'EQ-001'),
     (SELECT id FROM usuarios WHERE correo = 'docente@sigu.cl'),
     '2026-11-03 09:00:00', '2026-11-05 17:30:00', 'FINALIZADO');

-- Movimientos de ejemplo registrados por el encargado
INSERT INTO inv_movimientos (insumo_id, usuario_id, tipo, cantidad, fecha) VALUES
    ((SELECT id FROM inv_productos WHERE codigo = 'INS-001'),
     (SELECT id FROM usuarios WHERE correo = 'encargado@sigu.cl'), 'ENTRADA', 10, '2026-11-04 11:00:00'),
    ((SELECT id FROM inv_productos WHERE codigo = 'INS-002'),
     (SELECT id FROM usuarios WHERE correo = 'encargado@sigu.cl'), 'SALIDA', 4, '2026-11-06 15:20:00'),
    ((SELECT id FROM inv_productos WHERE codigo = 'INS-003'),
     (SELECT id FROM usuarios WHERE correo = 'encargado@sigu.cl'), 'SALIDA', 12, '2026-11-07 09:45:00');