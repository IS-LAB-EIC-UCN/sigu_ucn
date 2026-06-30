package cl.ucn.app.integration;

import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;
import cl.ucn.app.service.TutoriaService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TutoriaServiceIntegrationTest {

    private static TutoriaService service;
    private static Long tutorId;
    private static Long estudianteId;
    private static Long asignaturaId;
    private static Long tutoriaId;
    private static Long reservaId;

    @BeforeAll
    static void setUpAll() {
        service = new TutoriaService();

        service.registrarUsuarioTutoria("Tutor Integración", "tutor.integ@ucn.cl", "pass123", "TUTOR");
        service.registrarUsuarioTutoria("Estudiante Integración", "est.integ@ucn.cl", "pass123", "ESTUDIANTE");

        UsuarioRepository usuarioRepo = new UsuarioRepository();
        tutorId = usuarioRepo.findByCorreo("tutor.integ@ucn.cl").getId();
        estudianteId = usuarioRepo.findByCorreo("est.integ@ucn.cl").getId();

        service.crearAsignatura("INT101", "Asignatura Integración");
        AsignaturaRepository asigRepo = new AsignaturaRepository();
        asignaturaId = asigRepo.findByCodigo("INT101").getId();
    }

    @Test
    @Order(1)
    @DisplayName("INT-01: Publicar disponibilidad crea una tutoría en la BD")
    void publicarDisponibilidad_creaRegistroEnBD() {
        LocalDate manana = LocalDate.now().plusDays(1);

        service.publicarDisponibilidad(tutorId, asignaturaId, manana,
                LocalTime.of(14, 0), LocalTime.of(15, 0));

        List<Tutoria> tutorias = service.listarTutoriasPorTutor(tutorId);
        assertFalse(tutorias.isEmpty());

        Tutoria creada = tutorias.get(0);
        tutoriaId = creada.getId();

        assertEquals("DISPONIBLE", creada.getEstado());
        assertEquals(manana, creada.getFecha());
    }

    @Test
    @Order(2)
    @DisplayName("INT-03: Reservar tutoría cambia su estado a RESERVADA en la BD")
    void reservarTutoria_cambiaEstadoEnBD() {
        service.reservarTutoria(tutoriaId, estudianteId);

        TutoriaRepository repo = new TutoriaRepository();
        Tutoria tutoria = repo.findById(tutoriaId);

        assertEquals("RESERVADA", tutoria.getEstado());
    }

    @Test
    @Order(3)
    @DisplayName("INT-05: Listar historial del estudiante devuelve la reserva")
    void listarHistorialEstudiante_devuelveReserva() {
        List<TutoriaReserva> historial = service.listarHistorialEstudiante(estudianteId);

        assertFalse(historial.isEmpty());
        reservaId = historial.get(0).getId();

        assertEquals(tutoriaId, historial.get(0).getTutoria().getId());
    }

    @Test
    @Order(4)
    @DisplayName("INT-06: Registrar asistencia cambia estado a REALIZADA y guarda asistio=true")
    void registrarAsistencia_marcaAsistioYCambiaEstado() {
        service.registrarAsistencia(reservaId, true);

        TutoriaRepository tutoriaRepo = new TutoriaRepository();
        TutoriaReservaRepository reservaRepo = new TutoriaReservaRepository();

        assertEquals("REALIZADA", tutoriaRepo.findById(tutoriaId).getEstado());
        assertTrue(reservaRepo.findById(reservaId).getAsistio());
    }
}