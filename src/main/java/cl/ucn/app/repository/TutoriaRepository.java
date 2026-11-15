package cl.ucn.app.repository;

import cl.ucn.app.config.JpaTransaccion;
import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Tutoria;
import jakarta.persistence.EntityManager;

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

    Optional<Tutoria> findByIdForUpdate(EntityManager em, Long id);

    void save(EntityManager em, Tutoria tutoria);

    boolean existeConflictoTutor(EntityManager em, Long tutorId, LocalDateTime inicio, LocalDateTime fin);

    boolean existeConflictoEstudiante(EntityManager em, Long estudianteId, LocalDateTime inicio, LocalDateTime fin);

    long contarInscripcionesActivas(EntityManager em, Long tutoriaId);

    List<Tutoria> buscarDisponibles(TutoriaFiltro filtro);

    List<Tutoria> findByEstudianteId(Long estudianteId);

    default boolean existeConflictoTutor(Long tutorId, LocalDateTime inicio, LocalDateTime fin) {
        return JpaTransaccion.leer(em -> existeConflictoTutor(em, tutorId, inicio, fin));
    }

    default boolean existeConflictoEstudiante(Long estudianteId, LocalDateTime inicio, LocalDateTime fin) {
        return JpaTransaccion.leer(em -> existeConflictoEstudiante(em, estudianteId, inicio, fin));
    }

    default long contarInscripcionesActivas(Long tutoriaId) {
        return JpaTransaccion.leer(em -> contarInscripcionesActivas(em, tutoriaId));
    }
}
