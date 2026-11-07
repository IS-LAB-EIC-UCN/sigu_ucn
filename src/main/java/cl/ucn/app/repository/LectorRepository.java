package cl.ucn.app.repository;

import cl.ucn.app.model.Lector;
import java.util.List;
import java.util.Optional;

public interface LectorRepository {
    Optional<Lector> findById(Long id);
    Optional<Lector> findByRut(String rut);
    Optional<Lector> findByCorreo(String correo);
    List<Lector> findAll();
    Lector save(Lector lector);
    void update(Lector lector);
}
