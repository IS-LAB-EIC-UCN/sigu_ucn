package cl.ucn.app.repository;

import cl.ucn.app.model.Taller;
import java.util.List;

public interface ITallerRepository {
    void save(Taller taller);
    Taller findById(Long id);
    List<Taller> findAll();
    List<Taller> findByProfesor(Long profesorId);
    void delete(Long id);
    boolean existeConflicto(Long espacioId, Character bloque, java.time.LocalDate inicio, java.time.LocalDate fin);
}
