package cl.ucn.app.repository;

import cl.ucn.app.model.EstadoMulta;
import cl.ucn.app.model.Multa;
import java.util.List;
import java.util.Optional;

public interface MultaRepository {
    Optional<Multa> findById(Long id);
    Optional<Multa> findByPrestamoId(Long prestamoId);
    List<Multa> findByLectorId(Long lectorId);
    List<Multa> findByLectorIdAndEstado(Long lectorId, EstadoMulta estado);
    Multa save(Multa multa);
    void update(Multa multa);
}
