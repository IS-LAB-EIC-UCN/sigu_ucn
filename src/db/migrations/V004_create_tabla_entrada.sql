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

CREATE INDEX idx_entradas_movimiento_id ON entradas(movimiento_id);
CREATE INDEX idx_entradas_proveedor_id ON entradas(proveedor_id);