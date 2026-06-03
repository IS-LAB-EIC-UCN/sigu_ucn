CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          correo VARCHAR(120) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          activo BOOLEAN NOT NULL DEFAULT TRUE,
                          rol_id BIGINT NOT NULL,
                          CONSTRAINT fk_usuarios_roles
                              FOREIGN KEY (rol_id)
                                  REFERENCES roles(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT
);

CREATE TABLE espacios (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          tipo VARCHAR(50) NOT NULL,
                          capacidad INTEGER NOT NULL,
                          disponible BOOLEAN NOT NULL DEFAULT TRUE,
                          CONSTRAINT chk_espacios_capacidad
                              CHECK (capacidad > 0)
);

CREATE TABLE reservas (
                          id BIGSERIAL PRIMARY KEY,
                          fecha_reserva DATE NOT NULL,
                          hora_inicio TIME NOT NULL,
                          hora_fin TIME NOT NULL,
                          estado VARCHAR(30) NOT NULL,
                          usuario_id BIGINT NOT NULL,
                          espacio_id BIGINT NOT NULL,
                          CONSTRAINT fk_reservas_usuarios
                              FOREIGN KEY (usuario_id)
                                  REFERENCES usuarios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT fk_reservas_espacios
                              FOREIGN KEY (espacio_id)
                                  REFERENCES espacios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT chk_reservas_horas
                              CHECK (hora_inicio < hora_fin),
                          CONSTRAINT chk_reservas_estado
                              CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA', 'CANCELADA'))
);

CREATE TABLE talleres (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(150) NOT NULL,
                          descripcion TEXT,
                          cupos_totales INTEGER NOT NULL,
                          fecha_inicio DATE NOT NULL, -- Esto se refiere a inicio del taller completo como por ejemplo inicio en marzo final en julio
                          fecha_fin DATE NOT NULL,
                          estado VARCHAR(30) NOT NULL,
                          bloque_horario CHAR(1) NOT NULL, -- Se refiere a los bloques como letras A-F, Esta  bloqueado a ese rango
                          profesor_id BIGINT NOT NULL, -- id del profesor que hara el taller
                          espacio_id BIGINT, -- id del lugar en que se hara el taller 
                          CONSTRAINT fk_talleres_usuarios
                              FOREIGN KEY (profesor_id)
                                  REFERENCES usuarios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT fk_talleres_espacios
                              FOREIGN KEY (espacio_id)
                                  REFERENCES espacios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE SET NULL,
                          CONSTRAINT chk_talleres_cupos
                              CHECK (cupos_totales > 0),
                          CONSTRAINT chk_talleres_bloque
                              CHECK (bloque_horario IN ('A', 'B', 'C', 'D', 'E', 'F'))
);

CREATE TABLE inscripciones (
                               id BIGSERIAL PRIMARY KEY,
                               taller_id BIGINT NOT NULL, -- id del taller al que se inscribe
                               usuario_id BIGINT NOT NULL, -- id del usuario que se inscribe
                               fecha_inscripcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- fecha con dia y hora en la que se inscribio para facilitar lista de espera
                               estado VARCHAR(30) NOT NULL, -- Inscrito, en espera, cancelado
                               CONSTRAINT fk_inscripciones_talleres
                                   FOREIGN KEY (taller_id)
                                       REFERENCES talleres(id)
                                       ON UPDATE CASCADE
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_inscripciones_usuarios
                                   FOREIGN KEY (usuario_id)
                                       REFERENCES usuarios(id)
                                       ON UPDATE CASCADE
                                       ON DELETE CASCADE,
                               CONSTRAINT chk_inscripciones_estado
                                   CHECK (estado IN ('INSCRITO', 'EN_ESPERA', 'CANCELADO')),
                               CONSTRAINT uq_inscripciones_usuario_taller
                                   UNIQUE (taller_id, usuario_id)
);

CREATE INDEX idx_usuarios_rol_id ON usuarios(rol_id);
CREATE INDEX idx_reservas_usuario_id ON reservas(usuario_id);
CREATE INDEX idx_reservas_espacio_id ON reservas(espacio_id);
CREATE INDEX idx_reservas_fecha_reserva ON reservas(fecha_reserva);
CREATE INDEX idx_talleres_profesor_id ON talleres(profesor_id);
CREATE INDEX idx_talleres_espacio_id ON talleres(espacio_id);
CREATE INDEX idx_inscripciones_taller_id ON inscripciones(taller_id);
CREATE INDEX idx_inscripciones_usuario_id ON inscripciones(usuario_id);
