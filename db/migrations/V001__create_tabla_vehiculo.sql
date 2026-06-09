CREATE TABLE Vehiculo (
    patente VARCHAR(20) NOT NULL,
    propietario VARCHAR(100) NOT NULL,
    usuario_id BIGINT NOT NULL, -- Coincide con el tipo BIGSERIAL (BIGINT) de tu tabla usuarios
    
    -- Clave Primaria basada en la patente del vehículo
    CONSTRAINT pk_vehiculo 
        PRIMARY KEY (patente),
    
    -- Clave Foránea apuntando correctamente a tu tabla 'usuarios'
    CONSTRAINT fk_vehiculo_usuarios 
        FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);