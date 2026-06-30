
INSERT INTO roles (nombre) VALUES
    ('ADMIN'),
    ('DOCENTE'),
    ('ESTUDIANTE');

INSERT INTO usuarios (nombre_usuario, correo, password, activo, rol_id) VALUES
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

INSERT INTO categorias_ticket (nombre_categoria, contenido) VALUES
    ('Hardware', 'Problemas con equipos físicos (PC, impresoras, etc.)'),
    ('Software', 'Problemas con aplicaciones, sistemas operativos o licencias'),
    ('Redes', 'Problemas de conectividad, Wi-Fi, VPN, etc.'),
    ('Correo', 'Problemas con el correo institucional o listas de distribución'),
    ('Otros', 'Consultas generales no clasificadas');

INSERT INTO tickets (titulo, descripcion, prioridad, estado, fecha_creacion, solicitante_id, tecnico_id, categoria_id) VALUES
    ('PC no enciende', 'La computadora del laboratorio L1 no enciende al presionar el botón de encendido.', 'ALTA', 'ABIERTO', NOW(), 3, NULL, 1),
    ('Impresora atascada', 'La impresora de la biblioteca tiene un atasco de papel y no imprime.', 'MEDIA', 'EN_PROCESO', NOW(), 3, 2, 1),
    ('No puedo acceder a Moodle', 'Al intentar entrar a Moodle me sale error de autenticación.', 'ALTA', 'ABIERTO', NOW(), 3, NULL, 2),
    ('Error en sistema de calificaciones', 'No se pueden ingresar notas en el sistema académico. Muestra error 500.', 'ALTA', 'CERRADO', NOW() - INTERVAL '2 days', 3, 2, 2),
    ('Wi-Fi intermitente', 'La conexión Wi-Fi en el edificio A se cae cada 10 minutos.', 'MEDIA', 'ABIERTO', NOW() - INTERVAL '1 day', 3, NULL, 3),
    ('Solicitud de software', 'Necesito instalar MATLAB en la PC del laboratorio de ingeniería.', 'BAJA', 'ABIERTO', NOW(), 3, NULL, 2);

INSERT INTO comentarios (contenido, fecha, ticket_id, autor_id) VALUES
    ('Revisé la fuente de poder y el cableado, todo parece estar bien. Puede ser la placa madre.', NOW() - INTERVAL '2 hours', 1, 2),
    ('Ya pedí el repuesto para la impresora. Llega mañana.', NOW() - INTERVAL '1 hour', 2, 2),
    ('El problema de Moodle se debió a un cambio de contraseña. Ya se solucionó.', NOW() - INTERVAL '3 hours', 3, 2),
    ('El error en calificaciones ya fue corregido en el servidor.', NOW() - INTERVAL '1 day', 4, 2);