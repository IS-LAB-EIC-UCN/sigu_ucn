CREATE TABLE IF NOT EXISTS registros_estacionamiento (
                                                         id BIGSERIAL PRIMARY KEY,
                                                         reserva_id BIGINT NOT NULL,
                                                         fecha_hora_ingreso TIMESTAMP NOT NULL,
                                                         fecha_hora_salida TIMESTAMP,
                                                         tiempo_uso_minutos INTEGER,
                                                         estado VARCHAR(30) NOT NULL,

    CONSTRAINT fk_registro_reserva
    FOREIGN KEY (reserva_id)
    REFERENCES reservas(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,

    CONSTRAINT uq_registro_reserva
    UNIQUE (reserva_id),

    CONSTRAINT chk_registro_estado
    CHECK (estado IN ('EN_USO', 'FINALIZADO')),

    CONSTRAINT chk_registro_tiempo
    CHECK (tiempo_uso_minutos IS NULL OR tiempo_uso_minutos >= 0)
    );

CREATE INDEX IF NOT EXISTS idx_registros_reserva_id
    ON registros_estacionamiento(reserva_id);

CREATE INDEX IF NOT EXISTS idx_registros_fecha_ingreso
    ON registros_estacionamiento(fecha_hora_ingreso);

CREATE INDEX IF NOT EXISTS idx_registros_estado
    ON registros_estacionamiento(estado);