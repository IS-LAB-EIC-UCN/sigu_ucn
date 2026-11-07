package cl.ucn.app.repository;

import cl.ucn.app.model.EstadoPrestamo;
import cl.ucn.app.model.Prestamo;
import java.util.List;
import java.util.Optional;

public interface PrestamoRepository {
    Optional<Prestamo> findById(Long id);
    Optional<Prestamo> findActivoPorEjemplarId(Long ejemplarId);
    List<Prestamo> findByLectorId(Long lectorId);
    List<Prestamo> findByLectorIdAndEstado(Long lectorId, EstadoPrestamo estado);
    long countActivosByLectorId(Long lectorId);
    Prestamo save(Prestamo prestamo);
    void update(Prestamo prestamo);
}
