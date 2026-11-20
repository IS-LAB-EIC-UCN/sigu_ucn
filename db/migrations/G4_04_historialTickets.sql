-- RN08: Historial de cambios de cada ticket.

CREATE TABLE IF NOT EXISTS historial_tickets (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha_cambio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accion VARCHAR(150) NOT NULL,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30),
    CONSTRAINT fk_historial_tickets
        FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_historial_usuarios
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_historial_estado_anterior
        CHECK (estado_anterior IS NULL OR estado_anterior IN ('CREADO', 'ASIGNADO', 'EN_RESOLUCION', 'CERRADO')),
    CONSTRAINT chk_historial_estado_nuevo
        CHECK (estado_nuevo IS NULL OR estado_nuevo IN ('CREADO', 'ASIGNADO', 'EN_RESOLUCION', 'CERRADO'))
);

CREATE INDEX IF NOT EXISTS idx_historial_tickets_ticket_id ON historial_tickets(ticket_id);
