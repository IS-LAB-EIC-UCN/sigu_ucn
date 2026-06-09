CREATE TABLE usuarios (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL
);

CREATE TABLE espacios (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    capacidad INT NOT NULL,
    disponible BOOLEAN DEFAULT TRUE
);

CREATE TABLE reservas (
    id VARCHAR(36) PRIMARY KEY,
    usuario_id VARCHAR(36) REFERENCES usuarios(id),
    espacio_id VARCHAR(36) REFERENCES espacios(id),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL
);
