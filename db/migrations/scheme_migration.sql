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

                                          CONSTRAINT fk_disp_tutor FOREIGN KEY (tutor_id) REFERENCES usuarios(id) ON DELETE RESTRICT,
                                          CONSTRAINT fk_disp_asignatura FOREIGN KEY (asignatura_id) REFERENCES asignaturas(id) ON DELETE RESTRICT,
                                          CONSTRAINT chk_disp_horas CHECK (hora_inicio < hora_fin),
                                          CONSTRAINT chk_disp_estado CHECK (estado IN ('DISPONIBLE', 'RESERVADA'))
);


CREATE TABLE tutorias_reservas (
                                   id BIGSERIAL PRIMARY KEY,
                                   disponibilidad_id BIGINT UNIQUE NOT NULL,
                                   estudiante_id BIGINT NOT NULL,
                                   asistio BOOLEAN DEFAULT NULL, -- NULL = Pendiente, TRUE = Asistió, FALSE = Faltó
                                   creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_reserva_disp FOREIGN KEY (disponibilidad_id) REFERENCES disponibilidades_tutores(id) ON DELETE RESTRICT,
                                   CONSTRAINT fk_reserva_estudiante FOREIGN KEY (estudiante_id) REFERENCES usuarios(id) ON DELETE RESTRICT
);


CREATE INDEX idx_disp_tutor ON disponibilidades_tutores(tutor_id);
CREATE INDEX idx_disp_fecha ON disponibilidades_tutores(fecha);
CREATE INDEX idx_reserva_estudiante ON tutorias_reservas(estudiante_id);