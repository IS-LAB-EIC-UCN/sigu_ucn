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