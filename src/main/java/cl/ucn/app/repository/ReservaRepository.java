package cl.ucn.app.repository;

import cl.ucn.app.model.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository {
    void save(Reserva reserva);
    Optional<Reserva> findById(String id);
    List<Reserva> findByUsuarioId(String usuarioId);
    List<Reserva> findByEspacioId(String espacioId);
    List<Reserva> findAll();
    List<Reserva> findWithFilters(String usuarioId, String espacioId, String estado, LocalDateTime desde, LocalDateTime hasta);
    boolean hasOverlap(String espacioId, LocalDateTime inicio, LocalDateTime fin);
}
