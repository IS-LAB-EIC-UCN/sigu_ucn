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

CREATE INDEX idx_movimientosInventario_recurso_id ON movimientosInventario(recurso_id);
CREATE INDEX idx_movimientosInventario_fecha ON movimientosInventario(fecha, hora);