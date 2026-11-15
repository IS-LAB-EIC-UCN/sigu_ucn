package cl.ucn.app.repository;

import cl.ucn.app.model.InscripcionTutoria;

import java.util.Optional;
import java.util.List;

public interface InscripcionTutoriaRepository {

    Optional<InscripcionTutoria> findById(Long id);

    long countActivasByTutoriaId(Long tutoriaId);

    List<InscripcionTutoria> findByEstudianteId(Long estudianteId);

    void save(InscripcionTutoria inscripcion);
}
