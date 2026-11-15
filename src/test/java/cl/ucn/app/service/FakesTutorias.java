package cl.ucn.app.service;

import cl.ucn.app.config.Transaccion;
import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.EstadoInscripcion;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.InscripcionTutoria;
import cl.ucn.app.model.Tutor;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EstudianteRepository;
import cl.ucn.app.repository.InscripcionTutoriaRepository;
import cl.ucn.app.repository.TutorRepository;
import cl.ucn.app.repository.TutoriaRepository;
import jakarta.persistence.EntityManager;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

final class FakesTutorias {

    static final String CARRERA = "Ingeniería Civil en Computación e Informática";

    private FakesTutorias() {
    }

    static <T> T conId(T entidad, Long id) {
        try {
            Field campo = entidad.getClass().getDeclaredField("id");
            campo.setAccessible(true);
            campo.set(entidad, id);
            return entidad;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    static Asignatura asignatura(Long id, String codigo) {
        return conId(new Asignatura(codigo, "Asignatura " + codigo, CARRERA, 1), id);
    }

    static Estudiante estudiante(Long id, String correo) {
        Estudiante estudiante = conId(new Estudiante(correo, "Estudiante " + id, CARRERA, 1), id);
        estudiante.setUsuario(new Usuario());
        return estudiante;
    }

    static Tutor tutor(Long id, Estudiante estudiante, Asignatura... certificadas) {
        Set<Asignatura> asignaturas = new HashSet<>(List.of(certificadas));
        return conId(new Tutor(estudiante, asignaturas), id);
    }

    static class TransaccionFalsa implements Transaccion {
        @Override
        public <T> T ejecutar(Function<EntityManager, T> accion) {
            return accion.apply(null);
        }
    }

    static class TutoriaRepoFalso implements TutoriaRepository {
        Tutoria tutoria;
        boolean conflictoTutor;
        boolean conflictoEstudiante;
        long inscritos;
        RuntimeException errorAlGuardar;
        final List<Tutoria> guardadas = new ArrayList<>();

        @Override
        public Optional<Tutoria> findByIdForUpdate(EntityManager em, Long id) {
            return Optional.ofNullable(tutoria).filter(t -> t.getId().equals(id));
        }

        @Override
        public void save(EntityManager em, Tutoria nueva) {
            if (errorAlGuardar != null) {
                throw errorAlGuardar;
            }
            guardadas.add(nueva);
        }

        @Override
        public boolean existeConflictoTutor(EntityManager em, Long tutorId, LocalDateTime inicio, LocalDateTime fin) {
            return conflictoTutor;
        }

        @Override
        public boolean existeConflictoEstudiante(EntityManager em, Long estudianteId,
                                                 LocalDateTime inicio, LocalDateTime fin) {
            return conflictoEstudiante;
        }

        @Override
        public long contarInscripcionesActivas(EntityManager em, Long tutoriaId) {
            return inscritos;
        }

        @Override
        public long contarInscripcionesActivas(Long tutoriaId) {
            return inscritos;
        }

        @Override
        public Optional<Tutoria> findById(Long id) {
            return Optional.ofNullable(tutoria).filter(t -> t.getId().equals(id));
        }

        @Override
        public List<Tutoria> buscarDisponibles(TutoriaFiltro filtro) {
            return tutoria == null ? List.of() : List.of(tutoria);
        }

        @Override
        public long countByAsignatura(Long asignaturaId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long countByAsignaturaAndEstado(Long asignaturaId, EstadoTutoria estado) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void save(Tutoria tutoria) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Tutoria> findByTutorId(Long tutorId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Tutoria> findByEstudianteId(Long estudianteId) {
            throw new UnsupportedOperationException();
        }
    }

    static class TutorRepoFalso implements TutorRepository {
        Tutor tutor;
        boolean bloqueado;

        @Override
        public Optional<Tutor> findByUsuarioId(EntityManager em, Long usuarioId) {
            return Optional.ofNullable(tutor);
        }

        @Override
        public Optional<Tutor> findByUsuarioId(Long usuarioId) {
            return Optional.ofNullable(tutor);
        }

        @Override
        public void bloquear(EntityManager em, Tutor tutor) {
            bloqueado = true;
        }

        @Override
        public List<Tutor> findAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existsByEstudianteId(Long estudianteId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void save(Tutor tutor) {
            throw new UnsupportedOperationException();
        }
    }

    static class EstudianteRepoFalso implements EstudianteRepository {
        Estudiante estudiante;
        boolean cursa = true;

        @Override
        public Optional<Estudiante> findByUsuarioId(EntityManager em, Long usuarioId) {
            return Optional.ofNullable(estudiante);
        }

        @Override
        public Optional<Estudiante> findByUsuarioId(Long usuarioId) {
            return Optional.ofNullable(estudiante);
        }

        @Override
        public boolean cursaAsignatura(EntityManager em, Long estudianteId, Long asignaturaId) {
            return cursa;
        }

        @Override
        public Optional<Estudiante> findByCorreo(String correo) {
            throw new UnsupportedOperationException();
        }
    }

    static class InscripcionRepoFalso implements InscripcionTutoriaRepository {
        boolean yaInscrito;
        long activas;
        RuntimeException errorAlGuardar;
        final List<InscripcionTutoria> guardadas = new ArrayList<>();

        @Override
        public void save(EntityManager em, InscripcionTutoria inscripcion) {
            if (errorAlGuardar != null) {
                throw errorAlGuardar;
            }
            guardadas.add(inscripcion);
        }

        @Override
        public boolean existsByTutoriaIdAndEstudianteIdAndEstado(EntityManager em, Long tutoriaId,
                                                                 Long estudianteId, EstadoInscripcion estado) {
            return yaInscrito;
        }

        @Override
        public boolean existsByTutoriaIdAndEstudianteIdAndEstado(Long tutoriaId, Long estudianteId,
                                                                 EstadoInscripcion estado) {
            return yaInscrito;
        }

        @Override
        public long contarActivasPorTutoria(EntityManager em, Long tutoriaId) {
            return activas;
        }

        @Override
        public Optional<InscripcionTutoria> findById(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void save(InscripcionTutoria inscripcion) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<InscripcionTutoria> findByEstudianteId(Long estudianteId) {
            throw new UnsupportedOperationException();
        }
    }
}
