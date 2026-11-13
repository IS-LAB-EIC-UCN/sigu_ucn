package cl.ucn.app.repository;

import cl.ucn.app.model.InscripcionTutoria;

import java.util.Optional;

public interface InscripcionTutoriaRepository {

    Optional<InscripcionTutoria> findById(Long id);

    void save(InscripcionTutoria inscripcion);
}
