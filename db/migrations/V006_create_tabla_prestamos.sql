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

CREATE INDEX idx_prestamos_movimiento_id ON prestamos(movimiento_id);
CREATE INDEX idx_prestamos_usuario_id ON prestamos(usuario_id);