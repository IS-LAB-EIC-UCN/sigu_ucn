package cl.ucn.app.service;

import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.EstadoInscripcion;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.InscripcionTutoria;
import cl.ucn.app.model.ModalidadTutoria;
import cl.ucn.app.model.Tutor;
import cl.ucn.app.model.Tutoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static cl.ucn.app.service.FakesTutorias.*;
import static org.junit.jupiter.api.Assertions.*;

class ReservaTutoriaServiceTest {

    private static final Long USUARIO_ID = 20L;
    private static final Long TUTORIA_ID = 100L;

    private TutoriaRepoFalso tutoriaRepo;
    private InscripcionRepoFalso inscripcionRepo;
    private EstudianteRepoFalso estudianteRepo;
    private Estudiante estudiante;
    private Tutoria tutoria;
    private ReservaTutoriaService servicio;

    @BeforeEach
    void setUp() {
        Asignatura calculo = asignatura(1L, "DCCB-101");
        Tutor tutor = tutor(5L, estudiante(3L, "ana@alumnos.ucn.cl"), calculo);
        estudiante = estudiante(7L, "bruno@alumnos.ucn.cl");

        tutoria = conId(new Tutoria(tutor, calculo,
                LocalDateTime.of(2026, 11, 17, 10, 0), LocalDateTime.of(2026, 11, 17, 11, 0),
                ModalidadTutoria.ONLINE, null, 2), TUTORIA_ID);

        tutoriaRepo = new TutoriaRepoFalso();
        tutoriaRepo.tutoria = tutoria;
        inscripcionRepo = new InscripcionRepoFalso();
        estudianteRepo = new EstudianteRepoFalso();
        estudianteRepo.estudiante = estudiante;

        Clock reloj = Clock.fixed(Instant.parse("2026-11-16T09:00:00Z"), ZoneOffset.UTC);
        servicio = new ReservaTutoriaService(tutoriaRepo, inscripcionRepo, estudianteRepo,
                new TransaccionFalsa(), reloj);
    }

    @Test
    void reservaExitosaCreaLaInscripcionReservada() {
        InscripcionTutoria inscripcion = servicio.reservar(USUARIO_ID, TUTORIA_ID);

        assertEquals(1, inscripcionRepo.guardadas.size());
        assertSame(inscripcion, inscripcionRepo.guardadas.get(0));
        assertEquals(EstadoInscripcion.RESERVADA, inscripcion.getEstado());
        assertSame(tutoria, inscripcion.getTutoria());
        assertSame(estudiante, inscripcion.getEstudiante());
    }

    @Test
    void aceptaReservarElUltimoCupoDisponible() {
        inscripcionRepo.activas = 1;

        servicio.reservar(USUARIO_ID, TUTORIA_ID);

        assertEquals(1, inscripcionRepo.guardadas.size());
    }

    @Test
    void rechazaReservarSinCupoDisponible() {
        inscripcionRepo.activas = 2;

        TutoriaValidacionException error = rechazada();

        assertEquals("La tutoría no tiene cupos disponibles.", error.getMessage());
        assertTrue(inscripcionRepo.guardadas.isEmpty());
    }

    @Test
    void rechazaUnaAsignaturaQueElEstudianteNoCursa() {
        estudianteRepo.cursa = false;

        TutoriaValidacionException error = rechazada();

        assertTrue(error.getMessage().startsWith("No cursas la asignatura"));
        assertTrue(inscripcionRepo.guardadas.isEmpty());
    }

    @Test
    void rechazaUnChoqueDeHorarioConOtraTutoriaDelEstudiante() {
        tutoriaRepo.conflictoEstudiante = true;

        TutoriaValidacionException error = rechazada();

        assertTrue(error.getMessage().contains("se cruza con ese horario"));
        assertTrue(inscripcionRepo.guardadas.isEmpty());
    }

    @Test
    void rechazaUnaTutoriaCancelada() {
        tutoria.setEstado(EstadoTutoria.CANCELADA);

        TutoriaValidacionException error = rechazada();

        assertEquals("La tutoría fue cancelada.", error.getMessage());
        assertTrue(inscripcionRepo.guardadas.isEmpty());
    }

    @Test
    void rechazaUnaTutoriaRealizada() {
        tutoria.setEstado(EstadoTutoria.REALIZADA);

        assertEquals("La tutoría ya se realizó.", rechazada().getMessage());
    }

    @Test
    void rechazaUnaTutoriaVencida() {
        tutoria.setInicio(LocalDateTime.of(2026, 11, 16, 8, 0));
        tutoria.setFin(LocalDateTime.of(2026, 11, 16, 9, 0));

        assertEquals("La tutoría ya comenzó o está vencida.", rechazada().getMessage());
        assertTrue(inscripcionRepo.guardadas.isEmpty());
    }

    @Test
    void rechazaUnaReservaDuplicada() {
        inscripcionRepo.yaInscrito = true;

        assertEquals("Ya estás inscrito en esta tutoría.", rechazada().getMessage());
        assertTrue(inscripcionRepo.guardadas.isEmpty());
    }

    @Test
    void elTutorNoPuedeReservarSuPropiaTutoria() {
        estudianteRepo.estudiante = tutoria.getTutor().getEstudiante();

        assertEquals("No puedes reservar tu propia tutoría.", rechazada().getMessage());
    }

    @Test
    void rechazaUnaTutoriaInexistente() {
        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> servicio.reservar(USUARIO_ID, 999L));

        assertEquals("La tutoría solicitada no existe.", error.getMessage());
    }

    @Test
    void rechazaUnUsuarioSinPerfilDeEstudiante() {
        estudianteRepo.estudiante = null;

        assertTrue(rechazada().getMessage().startsWith("Tu cuenta no está asociada a un estudiante"));
    }

    @Test
    void traduceLaViolacionDelIndiceUnicoAUnMensajeDeNegocio() {
        inscripcionRepo.errorAlGuardar = new RuntimeException("no se pudo guardar",
                new SQLException("duplicate key value violates unique constraint", "23505"));

        assertEquals("Ya estás inscrito en esta tutoría.", rechazada().getMessage());
    }

    private TutoriaValidacionException rechazada() {
        return assertThrows(TutoriaValidacionException.class, () -> servicio.reservar(USUARIO_ID, TUTORIA_ID));
    }
}
