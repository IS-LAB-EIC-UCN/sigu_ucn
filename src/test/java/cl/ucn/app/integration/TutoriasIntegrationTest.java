package cl.ucn.app.integration;

import cl.ucn.app.config.JpaTransaccion;
import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.dto.TutoriaForm;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.EstadoInscripcion;
import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.InscripcionTutoria;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.repository.EstudianteRepository;
import cl.ucn.app.repository.EstudianteRepositoryImpl;
import cl.ucn.app.repository.InscripcionTutoriaRepository;
import cl.ucn.app.repository.InscripcionTutoriaRepositoryImpl;
import cl.ucn.app.repository.TutorRepository;
import cl.ucn.app.repository.TutorRepositoryImpl;
import cl.ucn.app.repository.TutoriaRepository;
import cl.ucn.app.repository.TutoriaRepositoryImpl;
import cl.ucn.app.service.ReservaTutoriaService;
import cl.ucn.app.service.TutoriaService;
import cl.ucn.app.service.TutoriaValidacionException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class TutoriasIntegrationTest {

    private static final String CORREO_TUTORA = "ana.rojas@alumnos.ucn.cl";
    private static final String CORREO_BRUNO = "bruno.castro@alumnos.ucn.cl";
    private static final String CORREO_CARLA = "carla.munoz@alumnos.ucn.cl";

    private final EstudianteRepository estudianteRepository = new EstudianteRepositoryImpl();
    private final TutorRepository tutorRepository = new TutorRepositoryImpl();
    private final TutoriaRepository tutoriaRepository = new TutoriaRepositoryImpl();
    private final InscripcionTutoriaRepository inscripcionRepository = new InscripcionTutoriaRepositoryImpl();
    private final TutoriaService tutoriaService = new TutoriaService();
    private final ReservaTutoriaService reservaService = new ReservaTutoriaService();

    private Long asignaturaId;
    private Long usuarioTutoraId;
    private Long usuarioBrunoId;
    private Long usuarioCarlaId;
    private LocalDate fecha;

    @BeforeEach
    void prepararDatos() {
        usuarioTutoraId = usuarioDe(CORREO_TUTORA);
        usuarioBrunoId = usuarioDe(CORREO_BRUNO);
        usuarioCarlaId = usuarioDe(CORREO_CARLA);
        fecha = LocalDate.now().plusDays(10);

        String codigo = "INT-" + System.nanoTime() % 100000;
        new JpaTransaccion().ejecutarSinResultado(em -> {
            Asignatura asignatura = new Asignatura(codigo, "Asignatura de integración", "Pruebas", 1);
            em.persist(asignatura);
            asignaturaId = asignatura.getId();

            // La tutora la certifica y los dos estudiantes la cursan.
            tutorRepository.findByUsuarioId(em, usuarioTutoraId).orElseThrow()
                    .getAsignaturasCertificadas().add(asignatura);
            estudianteRepository.findByUsuarioId(em, usuarioBrunoId).orElseThrow()
                    .getAsignaturasCursadas().add(asignatura);
            estudianteRepository.findByUsuarioId(em, usuarioCarlaId).orElseThrow()
                    .getAsignaturasCursadas().add(asignatura);
        });
    }

    @AfterEach
    void limpiarDatos() {
        if (asignaturaId == null) {
            return;
        }
        // Las filas de tut_tutor_asignaturas / tut_estudiante_asignaturas caen por ON DELETE CASCADE.
        new JpaTransaccion().ejecutarSinResultado(em -> {
            ejecutar(em, "DELETE FROM tut_inscripciones WHERE tutoria_id IN "
                    + "(SELECT id FROM tut_tutorias WHERE asignatura_id = :id)");
            ejecutar(em, "DELETE FROM tut_tutorias WHERE asignatura_id = :id");
            ejecutar(em, "DELETE FROM tut_asignaturas WHERE id = :id");
        });
    }

    @Test
    void persisteAsignaturaTutorYTutoriaYLaRecuperaConFiltros() {
        Tutoria publicada = tutoriaService.publicar(usuarioTutoraId, formulario("2"));

        assertNotNull(publicada.getId());

        List<Tutoria> porAsignatura = tutoriaRepository.buscarDisponibles(
                new TutoriaFiltro(asignaturaId, null, null));
        assertEquals(1, porAsignatura.size());
        assertEquals(publicada.getId(), porAsignatura.get(0).getId());
        assertEquals("Ana Rojas", porAsignatura.get(0).getTutor().getEstudiante().getNombre());

        Long tutorId = publicada.getTutor().getId();
        assertEquals(1, tutoriaRepository.buscarDisponibles(new TutoriaFiltro(asignaturaId, tutorId, fecha)).size());
        assertEquals(0, tutoriaRepository.buscarDisponibles(
                new TutoriaFiltro(asignaturaId, null, fecha.plusDays(1))).size());
        assertEquals(1, tutoriaRepository.findByTutorId(tutorId).stream()
                .filter(t -> t.getId().equals(publicada.getId())).count());
    }

    @Test
    void noPermitePublicarDosTutoriasEnLaMismaFranja() {
        tutoriaService.publicar(usuarioTutoraId, formulario("2"));

        // El servicio detecta el cruce y lo informa como error de negocio
        // (el índice único ux_tut_tutorias_franja_publicada es la barrera final en la base).
        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> tutoriaService.publicar(usuarioTutoraId, formulario("2")));
        assertEquals("horaInicio", error.getCampo());
    }

    @Test
    void reservaYVerificaCupoEInscripcionEnLaBase() {
        Tutoria tutoria = tutoriaService.publicar(usuarioTutoraId, formulario("1"));

        InscripcionTutoria inscripcion = reservaService.reservar(usuarioBrunoId, tutoria.getId());

        assertNotNull(inscripcion.getId());
        assertEquals(EstadoInscripcion.RESERVADA, inscripcion.getEstado());
        assertEquals(1, tutoriaRepository.contarInscripcionesActivas(tutoria.getId()));
        assertEquals(1, inscripcionRepository.contarActivasPorTutoria(tutoria.getId()));
        assertTrue(reservaService.tieneReservaActiva(usuarioBrunoId, tutoria.getId()));

        // Con capacidad 1 el cupo ya está agotado para otro estudiante.
        TutoriaValidacionException sinCupo = assertThrows(TutoriaValidacionException.class,
                () -> reservaService.reservar(usuarioCarlaId, tutoria.getId()));
        assertEquals("La tutoría no tiene cupos disponibles.", sinCupo.getMessage());
        assertEquals(1, inscripcionRepository.contarActivasPorTutoria(tutoria.getId()),
                "una reserva rechazada no debe dejar inscripciones parciales");
    }

    @Test
    void rechazaUnaReservaDuplicadaPorRestriccionUnica() {
        Tutoria tutoria = tutoriaService.publicar(usuarioTutoraId, formulario("5"));
        Estudiante bruno = estudianteRepository.findByUsuarioId(usuarioBrunoId).orElseThrow();

        inscripcionRepository.save(new InscripcionTutoria(tutoria, bruno));

        // Saltándose el servicio, el índice único parcial ux_tut_inscripciones_activa debe rechazarla.
        assertThrows(PersistenceException.class,
                () -> inscripcionRepository.save(new InscripcionTutoria(tutoria, bruno)));

        assertEquals(1, inscripcionRepository.contarActivasPorTutoria(tutoria.getId()));
    }

    // ---------------------------------------------------------------- auxiliares

    private Long usuarioDe(String correo) {
        Estudiante estudiante = estudianteRepository.findByCorreo(correo).orElseThrow(
                () -> new IllegalStateException("Falta el dato semilla " + correo
                        + ". Recree la base: docker compose down -v && docker compose up -d"));
        return estudiante.getUsuario().getId();
    }

    private TutoriaForm formulario(String capacidad) {
        return new TutoriaForm(String.valueOf(asignaturaId), fecha.toString(), "10:00", "11:00",
                "PRESENCIAL", "INT-1", capacidad);
    }

    private void ejecutar(EntityManager em, String sql) {
        em.createNativeQuery(sql).setParameter("id", asignaturaId).executeUpdate();
    }
}
