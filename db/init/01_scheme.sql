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

CREATE TABLE recursos (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          stock INTEGER NOT NULL,
                          tipo VARCHAR(30) NOT NULL,
                          CONSTRAINT chk_recursos_stock
                              CHECK (stock >= 0)
                          CONSTRAINT chk_recursos_tipo
                              CHECK (tipo IN ('INSUMO', 'EQUIPO'))
);

CREATE TABLE proveedores (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          correo VARCHAR(120) NOT NULL UNIQUE,
                          telefono VARCHAR(30) NOT NULL            
);

CREATE TABLE movimientosInventario (
                          id BIGSERIAL PRIMARY KEY,
                          recurso_id BIGINT NOT NULL,
                          cantidad INTEGER NOT NULL,
                          fecha DATE NOT NULL,
                          hora TIME NOT NULL,
                          CONSTRAINT fk_movimientosInventario_recursos
                              FOREIGN KEY (recurso_id)
                                  REFERENCES recursos(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT chk_movimientosInventario_cantidad
                              CHECK (cantidad >= 0)
);

CREATE TABLE entradas (
                          movimiento_id BIGINT NOT NULL UNIQUE,
                          proveedor_id BIGINT NOT NULL,
                          CONSTRAINT fk_entradas_movimientosInventario
                              FOREIGN KEY (movimiento_id)
                                  REFERENCES movimientosInventario(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT fk_entradas_proveedores
                              FOREIGN KEY (proveedor_id)
                                  REFERENCES proveedores(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT
);

CREATE TABLE salidas (
                          movimiento_id BIGINT NOT NULL UNIQUE,
                          CONSTRAINT fk_salidas_movimientosInventario
                              FOREIGN KEY (movimiento_id)
                                  REFERENCES movimientosInventario(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT
);

CREATE TABLE prestamos (
                          movimiento_id BIGINT NOT NULL UNIQUE,
                          usuario_id BIGINT NOT NULL,
                          estado VARCHAR(100) NOT NULL,
                          CONSTRAINT fk_prestamos_movimientosInventario
                              FOREIGN KEY (movimiento_id)
                                  REFERENCES movimientosInventario(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT
                          CONSTRAINT fk_prestamos_usuarios
                              FOREIGN KEY (usuario_id)
                                  REFERENCES usuarios(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT,
                          CONSTRAINT chk_prestamos_estado
                              CHECK (estado IN ('PRESTAMO PENDIENTE', 'PRESTAMO ACEPTADO', 'PRESTAMO RECHAZADO', 'DEVOLUCION PENDIENTE', 'DEVOLUCION CONFIRMADA'))
);

CREATE INDEX idx_usuarios_rol_id ON usuarios(rol_id);
CREATE INDEX idx_reservas_usuario_id ON reservas(usuario_id);
CREATE INDEX idx_reservas_espacio_id ON reservas(espacio_id);
CREATE INDEX idx_reservas_fecha_reserva ON reservas(fecha_reserva);
CREATE INDEX idx_movimientosInventario_recurso_id ON movimientosInventario(recurso_id);
CREATE INDEX idx_movimientosInventario_fecha ON movimientosInventario(fecha, hora);
CREATE INDEX idx_entradas_movimiento_id ON entradas(movimiento_id);
CREATE INDEX idx_entradas_proveedor_id ON entradas(proveedor_id);
CREATE INDEX idx_salidas_movimiento_id ON salidas(movimiento_id);
CREATE INDEX idx_prestamos_movimiento_id ON prestamos(movimiento_id);
CREATE INDEX idx_prestamos_usuario_id ON entradas(proveedor_id);