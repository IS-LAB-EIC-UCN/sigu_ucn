-- 1. Crear la tabla estacionamientos
CREATE TABLE estacionamientos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    
);