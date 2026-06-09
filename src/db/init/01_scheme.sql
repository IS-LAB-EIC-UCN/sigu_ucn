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

CREATE INDEX idx_usuarios_rol_id ON usuarios(rol_id);
CREATE INDEX idx_reservas_usuario_id ON reservas(usuario_id);
CREATE INDEX idx_reservas_espacio_id ON reservas(espacio_id);
CREATE INDEX idx_reservas_fecha_reserva ON reservas(fecha_reserva);

CREATE TABLE talleres (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    cupos_totales INTEGER NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    bloque_horario CHAR(1) NOT NULL,
    profesor_id BIGINT NOT NULL,
    espacio_id BIGINT,
    CONSTRAINT fk_talleres_usuarios
        FOREIGN KEY (profesor_id)
            REFERENCES usuarios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT fk_talleres_espacios
        FOREIGN KEY (espacio_id)
            REFERENCES espacios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);

CREATE TABLE inscripciones (
    id BIGSERIAL PRIMARY KEY,
    taller_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha_inscripcion TIMESTAMP NOT NULL,
    estado VARCHAR(30) NOT NULL,
    CONSTRAINT fk_inscripciones_talleres
        FOREIGN KEY (taller_id)
            REFERENCES talleres(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT fk_inscripciones_usuarios
        FOREIGN KEY (usuario_id)
            REFERENCES usuarios(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
    CONSTRAINT uk_inscripciones_taller_usuario UNIQUE (taller_id, usuario_id)
);