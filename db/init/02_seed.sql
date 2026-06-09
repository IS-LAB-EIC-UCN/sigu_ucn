INSERT INTO usuarios (id, nombre, email, password, rol) VALUES
('u1', 'Admin', 'admin@ucn.cl', 'admin123', 'ADMINISTRADOR'),
('u2', 'Juan Perez', 'juan.perez@alumnos.ucn.cl', 'perez123', 'ESTUDIANTE');

INSERT INTO espacios (id, nombre, tipo, capacidad, disponible) VALUES
('e1', 'Sala de Computación 1', 'SALA_COMPUTACION', 30, TRUE),
('e2', 'Auditorio K170', 'AUDITORIO', 100, TRUE),
('e3', 'Sala de Estudio A', 'SALA_ESTUDIO', 10, TRUE);
