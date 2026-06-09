ALTER TABLE reservas
ADD COLUMN vehiculo_patente VARCHAR(20) NOT NULL;

ALTER TABLE reservas
ADD CONSTRAINT fk_reservas_vehiculo
    FOREIGN KEY (vehiculo_patente)
    REFERENCES Vehiculo(patente)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

-- Índice para mejorar el rendimiento de búsquedas de reservas por patente
CREATE INDEX idx_reservas_vehiculo_patente ON reservas(vehiculo_patente);