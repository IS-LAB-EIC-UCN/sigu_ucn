ALTER TABLE espacios 
ADD COLUMN estacionamiento_id BIGINT NOT NULL,
ADD CONSTRAINT fk_espacios_estacionamientos
    FOREIGN KEY (estacionamiento_id)
    REFERENCES estacionamientos(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;