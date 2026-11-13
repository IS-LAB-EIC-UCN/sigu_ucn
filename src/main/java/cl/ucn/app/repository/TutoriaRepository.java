package cl.ucn.app.repository;

import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Tutoria;

import java.util.Optional;

public interface TutoriaRepository {

    Optional<Tutoria> findById(Long id);

    long countByAsignatura(Long asignaturaId);

    long countByAsignaturaAndEstado(Long asignaturaId, EstadoTutoria estado);

    void save(Tutoria tutoria);
}
