package cl.ucn.app.exception;

import cl.ucn.app.model.EstadoReserva;

/**
 * Se lanza cuando se intenta cambiar una reserva a un estado no permitido
 * desde su estado actual (por ejemplo, aprobar una reserva CANCELADA - RN-03).
 */
public class TransicionEstadoInvalidaException extends RuntimeException {

    private final EstadoReserva estadoActual;
    private final EstadoReserva estadoDestino;

    public TransicionEstadoInvalidaException(EstadoReserva estadoActual,
                                             EstadoReserva estadoDestino,
                                             String mensaje) {
        super(mensaje);
        this.estadoActual = estadoActual;
        this.estadoDestino = estadoDestino;
    }

    public EstadoReserva getEstadoActual() {
        return estadoActual;
    }

    public EstadoReserva getEstadoDestino() {
        return estadoDestino;
    }
}
