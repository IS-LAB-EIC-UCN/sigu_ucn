package cl.ucn.app.service;

import cl.ucn.app.exception.TransicionEstadoInvalidaException;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.repository.ReservaRepository;

import java.util.List;
import java.util.Objects;

/**
 * Servicio de administración para revisar, aprobar y rechazar reservas pendientes (HU-02, RF-07).
 */
public class AdminReservaService {

    private static final int LONGITUD_MAXIMA_MOTIVO = 255;

    private final ReservaRepository reservaRepository;

    public AdminReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = Objects.requireNonNull(reservaRepository);
    }

    public List<Reserva> listarPendientes() {
        return reservaRepository.buscarPendientes();
    }

    public Reserva aprobar(Long reservaId) {
        if (reservaId == null) {
            throw new ReservaException("La reserva no existe.");
        }
        Reserva reserva = reservaRepository.buscarPorId(reservaId);
        if (reserva == null) {
            throw new ReservaException("La reserva no existe.");
        }
        try {
            reserva.aprobar();
        } catch (TransicionEstadoInvalidaException e) {
            throw new ReservaException(
                    "Solo se pueden aprobar reservas en estado PENDIENTE; la reserva está "
                            + reserva.getEstado() + ".");
        }
        reservaRepository.actualizar(reserva);
        return reserva;
    }

    public Reserva rechazar(Long reservaId, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new ReservaException("Debe indicar el motivo del rechazo.");
        }
        String motivoLimpio = motivo.trim();
        if (motivoLimpio.length() > LONGITUD_MAXIMA_MOTIVO) {
            throw new ReservaException("El motivo no puede superar 255 caracteres.");
        }
        if (reservaId == null) {
            throw new ReservaException("La reserva no existe.");
        }
        Reserva reserva = reservaRepository.buscarPorId(reservaId);
        if (reserva == null) {
            throw new ReservaException("La reserva no existe.");
        }
        try {
            reserva.rechazar(motivoLimpio);
        } catch (TransicionEstadoInvalidaException e) {
            throw new ReservaException(
                    "Solo se pueden rechazar reservas en estado PENDIENTE; la reserva está "
                            + reserva.getEstado() + ".");
        }
        reservaRepository.actualizar(reserva);
        return reserva;
    }
}
