

CREATE TABLE asignaturas (
                             id BIGSERIAL PRIMARY KEY,
                             codigo VARCHAR(20) NOT NULL UNIQUE,
                             nombre VARCHAR(100) NOT NULL
);

CREATE TABLE disponibilidades_tutores (
                                          id BIGSERIAL PRIMARY KEY,

                                          tutor_id BIGINT NOT NULL,

                                          asignatura_id BIGINT NOT NULL,

                                          fecha DATE NOT NULL,

                                          hora_inicio TIME NOT NULL,

                                          hora_fin TIME NOT NULL,

                                          estado VARCHAR(30) NOT NULL DEFAULT 'DISPONIBLE',

                                          CONSTRAINT fk_tutorias_tutor
                                              FOREIGN KEY (tutor_id)
                                                  REFERENCES usuarios(id)
                                                  ON UPDATE CASCADE
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT fk_tutorias_asignatura
                                              FOREIGN KEY (asignatura_id)
                                                  REFERENCES asignaturas(id)
                                                  ON UPDATE CASCADE
                                                  ON DELETE RESTRICT,

                                          CONSTRAINT chk_tutoria_horas
                                              CHECK (hora_inicio < hora_fin),

                                          CONSTRAINT chk_tutoria_estado
                                              CHECK (
                                                  estado IN (
                                                             'DISPONIBLE',
                                                             'RESERVADA',
                                                             'CANCELADA',
                                                             'REALIZADA'
                                                      )
                                                  )
);

CREATE TABLE tutorias_reservas (
                                   id BIGSERIAL PRIMARY KEY,

                                   disponibilidad_id BIGINT NOT NULL UNIQUE,

                                   estudiante_id BIGINT NOT NULL,

                                   asistio BOOLEAN DEFAULT NULL,

                                   creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_reserva_tutoria
                                       FOREIGN KEY (disponibilidad_id)
                                           REFERENCES disponibilidades_tutores(id)
                                           ON UPDATE CASCADE
                                           ON DELETE RESTRICT,

                                   CONSTRAINT fk_reserva_estudiante
                                       FOREIGN KEY (estudiante_id)
                                           REFERENCES usuarios(id)
                                           ON UPDATE CASCADE
                                           ON DELETE RESTRICT
);

CREATE INDEX idx_tutorias_tutor_id
    ON disponibilidades_tutores(tutor_id);

CREATE INDEX idx_tutorias_asignatura_id
    ON disponibilidades_tutores(asignatura_id);

CREATE INDEX idx_tutorias_fecha
    ON disponibilidades_tutores(fecha);

CREATE INDEX idx_tutorias_reservas_estudiante
    ON tutorias_reservas(estudiante_id);