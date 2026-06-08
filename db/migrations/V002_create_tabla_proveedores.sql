CREATE TABLE proveedores (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          correo VARCHAR(120) NOT NULL UNIQUE,
                          telefono VARCHAR(30) NOT NULL            
);