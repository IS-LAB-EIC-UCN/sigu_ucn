CREATE TABLE reservas_estacionamiento (
    id BIGSERIAL PRIMARY KEY,

    fecha_hora_inicio TIMESTAMP NOT NULL,
    fecha_hora_fin TIMESTAMP NOT NULL,

    estado VARCHAR(30) NOT NULL,

    usuario_id BIGINT NOT NULL,
    vehiculo_id BIGINT NOT NULL,
    estacionamiento_id BIGINT NOT NULL,

    CONSTRAINT fk_reservas_est_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_reservas_est_vehiculo
        FOREIGN KEY (vehiculo_id)
        REFERENCES vehiculos(id),

    CONSTRAINT fk_reservas_est_estacionamiento
        FOREIGN KEY (estacionamiento_id)
        REFERENCES estacionamientos(id),

    CONSTRAINT chk_reservas_est_horario
        CHECK (fecha_hora_fin > fecha_hora_inicio)
);