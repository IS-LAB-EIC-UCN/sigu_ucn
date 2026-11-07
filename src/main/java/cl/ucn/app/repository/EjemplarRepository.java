package cl.ucn.app.repository;

import cl.ucn.app.model.Ejemplar;
import cl.ucn.app.model.EstadoEjemplar;
import java.util.List;
import java.util.Optional;

public interface EjemplarRepository {
    Optional<Ejemplar> findById(Long id);
    Optional<Ejemplar> findByCodigoInventario(String codigoInventario);
    List<Ejemplar> findByLibroId(Long libroId);
    List<Ejemplar> findByLibroIdAndEstado(Long libroId, EstadoEjemplar estado);
    Ejemplar save(Ejemplar ejemplar);
    void update(Ejemplar ejemplar);
}
