package cl.ucn.app.service;

import cl.ucn.app.model.Reserva;
import cl.ucn.app.repository.ReservaRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReservaService {
    private static final String PENDIENTE = "PENDIENTE";
    private static final String APROBADA = "APROBADA";
    private static final String CANCELADA = "CANCELADA";
    private final ReservaRepository reservaRepository;
    private final Clock reloj;
    public ReservaService(ReservaRepository reservaRepository, Clock reloj) {
        this.reservaRepository = Objects.requireNonNull(reservaRepository);
        this.reloj = Objects.requireNonNull(reloj);
    }
    /** Reservas del usuario, de la más reciente a la más antigua. Los filtros nulos se ignoran. */
    public List<Reserva> listarMisReservas(Long usuarioId, Long espacioId, LocalDate fecha, String estado) {
        return reservaRepository.buscarPorUsuario(usuarioId, espacioId, fecha, estado);
    }
    /** Indica si el botón Cancelar debe mostrarse para esta reserva. */
    public boolean puedeCancelarse(Reserva reserva) {
        return motivoNoCancelable(reserva).isEmpty();
    }

    public void cancelar(Long reservaId, Long usuarioId) {
        Reserva reserva = reservaRepository.buscarPorId(reservaId);
        if (reserva == null) {
            throw new ReservaException("La reserva solicitada no existe.");
        }
        if (!reserva.getUsuario().getId().equals(usuarioId)) {
            throw new ReservaException("No puede cancelar una reserva que pertenece a otro usuario.");
        }
        motivoNoCancelable(reserva).ifPresent(motivo -> {
            throw new ReservaException(motivo);
        });
        reservaRepository.actualizarEstado(reservaId, CANCELADA);
    }
    private Optional<String> motivoNoCancelable(Reserva reserva) {
        String estado = reserva.getEstado();
        if (!PENDIENTE.equals(estado) && !APROBADA.equals(estado)) {
            return Optional.of("Solo se pueden cancelar reservas pendientes o aprobadas; " + "esta reserva está " + estado + ".");
        }
        LocalDateTime inicio = LocalDateTime.of(reserva.getFechaReserva(), reserva.getHoraInicio());
        if (!inicio.isAfter(LocalDateTime.now(reloj))) {
            return Optional.of("No se puede cancelar una reserva que ya comenzó.");
        }
        return Optional.empty();
    }
}