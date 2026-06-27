CREATE TABLE IF NOT EXISTS vehiculos (
                                         id BIGSERIAL PRIMARY KEY,
                                         patente VARCHAR(10) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_vehiculo_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id)
    );

ALTER TABLE reservas
    ADD COLUMN IF NOT EXISTS vehiculo_id BIGINT;

ALTER TABLE reservas
    ADD CONSTRAINT fk_reserva_vehiculo
        FOREIGN KEY (vehiculo_id)
            REFERENCES vehiculos(id);

ALTER TABLE reservas
    ADD COLUMN IF NOT EXISTS puesto_numero INTEGER;