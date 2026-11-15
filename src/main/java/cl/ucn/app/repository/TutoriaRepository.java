package cl.ucn.app.repository;

import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.dto.TutoriaFiltro;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TutoriaRepository {

    Optional<Tutoria> findById(Long id);

    List<Tutoria> buscarDisponibles(TutoriaFiltro filtro, LocalDateTime ahora);

    List<Tutoria> findByTutorId(Long tutorId);

    long countByAsignatura(Long asignaturaId);

    long countByAsignaturaAndEstado(Long asignaturaId, EstadoTutoria estado);

    void save(Tutoria tutoria);
}
