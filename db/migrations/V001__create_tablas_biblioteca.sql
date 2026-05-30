-- Tabla de usuarios de la biblioteca
CREATE TABLE usuario (
                        id SERIAL PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL,
                        correo VARCHAR(100) NOT NULL UNIQUE,
                        rut VARCHAR(12) NOT NULL UNIQUE,
                        bloqueado BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabla de libros
CREATE TABLE libro (
                       id SERIAL PRIMARY KEY,
                       titulo VARCHAR(200) NOT NULL,
                       autor VARCHAR(100) NOT NULL,
                       categoria VARCHAR(100) NOT NULL,
                       isbn VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla de ejemplares físicos de un libro
CREATE TABLE ejemplar (
                          id SERIAL PRIMARY KEY,
                          estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
                          libro_id INTEGER NOT NULL REFERENCES libro(id)
);

-- Tabla de préstamos
CREATE TABLE prestamo (
                          id SERIAL PRIMARY KEY,
                          fecha_inicio DATE NOT NULL,
                          fecha_vencimiento DATE NOT NULL,
                          fecha_devolucion DATE,
                          estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
                          usuario_id INTEGER NOT NULL REFERENCES usuario(id),
                          ejemplar_id INTEGER NOT NULL REFERENCES ejemplar(id)
);

-- Tabla de multas
CREATE TABLE multa (
                       id SERIAL PRIMARY KEY,
                       dias_atraso INTEGER NOT NULL,
                       monto DECIMAL(10,2) NOT NULL,
                       pagada BOOLEAN NOT NULL DEFAULT FALSE,
                       prestamo_id INTEGER NOT NULL REFERENCES prestamo(id)
);