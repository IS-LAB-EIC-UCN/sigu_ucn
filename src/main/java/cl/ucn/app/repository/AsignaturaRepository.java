package cl.ucn.app.repository;

import cl.ucn.app.model.Asignatura;

import java.util.List;
import java.util.Optional;

public interface AsignaturaRepository {

    List<Asignatura> findAll();

    Optional<Asignatura> findById(Long id);

    Optional<Asignatura> findByCodigo(String codigo);

    List<Asignatura> findByIds(List<Long> ids);

    void save(Asignatura asignatura);

    void update(Asignatura asignatura);

    void delete(Long id);
}
