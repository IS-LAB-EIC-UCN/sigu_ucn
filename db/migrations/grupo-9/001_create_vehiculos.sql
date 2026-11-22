CREATE TABLE vehiculos (
    id BIGSERIAL PRIMARY KEY,
    patente VARCHAR(10) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT fk_vehiculos_usuarios
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id)
);