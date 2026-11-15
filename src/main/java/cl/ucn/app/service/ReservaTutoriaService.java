package cl.ucn.app.service;

import cl.ucn.app.config.JpaTransaccion;
import cl.ucn.app.config.Transaccion;
import cl.ucn.app.model.EstadoInscripcion;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.InscripcionTutoria;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.repository.EstudianteRepository;
import cl.ucn.app.repository.EstudianteRepositoryImpl;
import cl.ucn.app.repository.InscripcionTutoriaRepository;
import cl.ucn.app.repository.InscripcionTutoriaRepositoryImpl;
import cl.ucn.app.repository.TutoriaRepository;
import cl.ucn.app.repository.TutoriaRepositoryImpl;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;

public class ReservaTutoriaService {

    private static final String SQLSTATE_UNICIDAD = "23505";

    private final TutoriaRepository tutoriaRepository;
    private final InscripcionTutoriaRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final Transaccion transaccion;
    private final Clock reloj;

    public ReservaTutoriaService(TutoriaRepository tutoriaRepository,
                                 InscripcionTutoriaRepository inscripcionRepository,
                                 EstudianteRepository estudianteRepository,
                                 Transaccion transaccion, Clock reloj) {
        this.tutoriaRepository = tutoriaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.transaccion = transaccion;
        this.reloj = reloj;
    }

    public ReservaTutoriaService() {
        this(new TutoriaRepositoryImpl(), new InscripcionTutoriaRepositoryImpl(),
                new EstudianteRepositoryImpl(), new JpaTransaccion(), Clock.systemDefaultZone());
    }

    public boolean tieneReservaActiva(Long usuarioId, Long tutoriaId) {
        if (usuarioId == null || tutoriaId == null) {
            return false;
        }
        return estudianteRepository.findByUsuarioId(usuarioId)
                .map(e -> inscripcionRepository.existsByTutoriaIdAndEstudianteIdAndEstado(
                        tutoriaId, e.getId(), EstadoInscripcion.RESERVADA))
                .orElse(false);
    }

    public InscripcionTutoria reservar(Long usuarioId, Long tutoriaId) {
        if (tutoriaId == null) {
            throw new TutoriaValidacionException(null, "La tutoría solicitada no existe.");
        }

        try {
            return transaccion.ejecutar(em -> {
                Estudiante estudiante = estudianteRepository.findByUsuarioId(em, usuarioId).orElseThrow(
                        () -> new TutoriaValidacionException(null,
                                "Tu cuenta no está asociada a un estudiante, por lo que no puede reservar."));

                Tutoria tutoria = tutoriaRepository.findByIdForUpdate(em, tutoriaId).orElseThrow(
                        () -> new TutoriaValidacionException(null, "La tutoría solicitada no existe."));

                validarVigente(tutoria);

                if (tutoria.getTutor().getEstudiante().getId().equals(estudiante.getId())) {
                    throw new TutoriaValidacionException(null, "No puedes reservar tu propia tutoría.");
                }
                if (inscripcionRepository.existsByTutoriaIdAndEstudianteIdAndEstado(
                        em, tutoriaId, estudiante.getId(), EstadoInscripcion.RESERVADA)) {
                    throw new TutoriaValidacionException(null, "Ya estás inscrito en esta tutoría.");
                }
                if (!estudianteRepository.cursaAsignatura(em, estudiante.getId(), tutoria.getAsignatura().getId())) {
                    throw new TutoriaValidacionException(null,
                            "No cursas la asignatura " + tutoria.getAsignatura().getCodigo() + ".");
                }
                if (inscripcionRepository.contarActivasPorTutoria(em, tutoriaId) >= tutoria.getCapacidad()) {
                    throw new TutoriaValidacionException(null, "La tutoría no tiene cupos disponibles.");
                }
                if (tutoriaRepository.existeConflictoEstudiante(
                        em, estudiante.getId(), tutoria.getInicio(), tutoria.getFin())) {
                    throw new TutoriaValidacionException(null,
                            "Ya tienes otra tutoría reservada que se cruza con ese horario.");
                }

                InscripcionTutoria inscripcion = new InscripcionTutoria(tutoria, estudiante);
                inscripcionRepository.save(em, inscripcion);
                return inscripcion;
            });
        } catch (RuntimeException e) {
            if (esViolacionDeUnicidad(e)) {
                throw new TutoriaValidacionException(null, "Ya estás inscrito en esta tutoría.");
            }
            throw e;
        }
    }

    private void validarVigente(Tutoria tutoria) {
        if (tutoria.getEstado() == EstadoTutoria.CANCELADA) {
            throw new TutoriaValidacionException(null, "La tutoría fue cancelada.");
        }
        if (tutoria.getEstado() == EstadoTutoria.REALIZADA) {
            throw new TutoriaValidacionException(null, "La tutoría ya se realizó.");
        }
        if (!tutoria.getInicio().isAfter(LocalDateTime.now(reloj))) {
            throw new TutoriaValidacionException(null, "La tutoría ya comenzó o está vencida.");
        }
    }

    private static boolean esViolacionDeUnicidad(Throwable error) {
        for (Throwable actual = error; actual != null; actual = actual.getCause()) {
            if (actual instanceof SQLException sql && SQLSTATE_UNICIDAD.equals(sql.getSQLState())) {
                return true;
            }
            if (actual.getCause() == actual) {
                break;
            }
        }
        return false;
    }
}
