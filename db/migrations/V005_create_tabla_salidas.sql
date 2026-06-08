CREATE TABLE salidas (
                          movimiento_id BIGINT NOT NULL UNIQUE,
                          CONSTRAINT fk_salidas_movimientosInventario
                              FOREIGN KEY (movimiento_id)
                                  REFERENCES movimientosInventario(id)
                                  ON UPDATE CASCADE
                                  ON DELETE RESTRICT
);

CREATE INDEX idx_salidas_movimiento_id ON salidas(movimiento_id);