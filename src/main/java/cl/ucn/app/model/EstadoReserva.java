package cl.ucn.app.model;

/**
 * Estados posibles de una reserva de sala (Grupo 1).
 * Los valores coinciden con el CHECK chk_reservas_estado de la tabla reservas.
 */
public enum EstadoReserva {
    PENDIENTE,
    APROBADA,
    RECHAZADA,
    CANCELADA;

    /**
     * RN-05: una reserva es activa si está PENDIENTE o APROBADA.
     * Solo las reservas activas ocupan el bloque horario de la sala.
     */
    public boolean esActiva() {
        return this == PENDIENTE || this == APROBADA;
    }
}
