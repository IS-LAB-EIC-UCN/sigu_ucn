package cl.ucn.app.repository;

import cl.ucn.app.model.Reserva;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository {
    void save(Reserva reserva);
    Optional<Reserva> findById(Long id);
    List<Reserva> findByUsuarioId(Long usuarioId);
    List<Reserva> findByEspacioId(Long espacioId);
    List<Reserva> findAll();
    List<Reserva> findWithFilters(Long usuarioId, Long espacioId, String estado, LocalDateTime desde, LocalDateTime hasta);
    boolean hasOverlap(Long espacioId, LocalDate fechaReserva, LocalTime horaInicio, LocalTime horaFin);
}
