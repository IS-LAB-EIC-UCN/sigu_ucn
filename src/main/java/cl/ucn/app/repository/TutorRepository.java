package cl.ucn.app.repository;

import cl.ucn.app.model.Tutor;

import java.util.List;
import java.util.Optional;

public interface TutorRepository {

    List<Tutor> findAll();

    Optional<Tutor> findByUsuarioId(Long usuarioId);

    boolean existsByEstudianteId(Long estudianteId);

    void save(Tutor tutor);
}
