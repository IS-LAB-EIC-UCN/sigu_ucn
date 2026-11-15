package cl.ucn.app.service;

import cl.ucn.app.dto.TutoriaForm;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.ModalidadTutoria;
import cl.ucn.app.model.Tutor;
import cl.ucn.app.model.Tutoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static cl.ucn.app.service.FakesTutorias.*;
import static org.junit.jupiter.api.Assertions.*;

class TutoriaServicePublicarTest {

    private static final Long USUARIO_ID = 10L;
    private static final String MANANA = "2026-11-17";

    private TutoriaRepoFalso tutoriaRepo;
    private TutorRepoFalso tutorRepo;
    private Asignatura calculo;
    private TutoriaService servicio;

    @BeforeEach
    void setUp() {
        calculo = asignatura(1L, "DCCB-101");
        Tutor tutor = tutor(5L, estudiante(3L, "ana@alumnos.ucn.cl"), calculo);

        tutoriaRepo = new TutoriaRepoFalso();
        tutorRepo = new TutorRepoFalso();
        tutorRepo.tutor = tutor;

        Clock reloj = Clock.fixed(Instant.parse("2026-11-16T09:00:00Z"), ZoneOffset.UTC);
        servicio = new TutoriaService(tutoriaRepo, tutorRepo, new TransaccionFalsa(), reloj);
    }

    @Test
    void publicaUnaTutoriaValidaComoPublicada() {
        Tutoria tutoria = servicio.publicar(USUARIO_ID, form("1", MANANA, "10:00", "11:30", "PRESENCIAL", "Y-103", "8"));

        assertEquals(1, tutoriaRepo.guardadas.size());
        assertSame(tutoria, tutoriaRepo.guardadas.get(0));
        assertEquals(EstadoTutoria.PUBLICADA, tutoria.getEstado());
        assertEquals(ModalidadTutoria.PRESENCIAL, tutoria.getModalidad());
        assertEquals("Y-103", tutoria.getSala());
        assertEquals(8, tutoria.getCapacidad().intValue());
        assertEquals(90, tutoria.getDuracionMinutos());
        assertTrue(tutorRepo.bloqueado, "debe bloquear al tutor antes de revisar el cruce de horarios");
    }

    @Test
    void unaTutoriaOnlineNoGuardaSala() {
        Tutoria tutoria = servicio.publicar(USUARIO_ID, form("1", MANANA, "10:00", "11:00", "ONLINE", "Y-103", "5"));

        assertNull(tutoria.getSala());
    }

    @Test
    void rechazaUnaHoraDeInicioAnteriorALas7() {
        TutoriaValidacionException error = rechazada(form("1", MANANA, "06:30", "08:00", "ONLINE", "", "5"));

        assertEquals("horaInicio", error.getCampo());
        assertTrue(tutoriaRepo.guardadas.isEmpty());
    }

    @Test
    void rechazaUnaHoraDeTerminoPosteriorALas22() {
        TutoriaValidacionException error = rechazada(form("1", MANANA, "21:00", "22:30", "ONLINE", "", "5"));

        assertEquals("horaFin", error.getCampo());
    }

    @Test
    void aceptaUnaTutoriaQueTerminaExactamenteALas22() {
        servicio.publicar(USUARIO_ID, form("1", MANANA, "21:00", "22:00", "ONLINE", "", "5"));

        assertEquals(1, tutoriaRepo.guardadas.size());
    }

    @Test
    void rechazaUnaDuracionMenorA30Minutos() {
        TutoriaValidacionException error = rechazada(form("1", MANANA, "10:00", "10:20", "ONLINE", "", "5"));

        assertEquals("horaFin", error.getCampo());
    }

    @Test
    void aceptaUnaDuracionDeExactamente30Minutos() {
        servicio.publicar(USUARIO_ID, form("1", MANANA, "10:00", "10:30", "ONLINE", "", "5"));

        assertEquals(1, tutoriaRepo.guardadas.size());
    }

    @Test
    void rechazaUnaHoraDeTerminoAnteriorALaDeInicio() {
        TutoriaValidacionException error = rechazada(form("1", MANANA, "11:00", "10:00", "ONLINE", "", "5"));

        assertEquals("horaFin", error.getCampo());
    }

    @Test
    void rechazaPublicarConMenosDe3HorasDeAnticipacion() {
        // Ahora son las 09:00; 11:30 es solo 2,5 horas después.
        TutoriaValidacionException error = rechazada(form("1", "2026-11-16", "11:30", "12:30", "ONLINE", "", "5"));

        assertEquals("horaInicio", error.getCampo());
    }

    @Test
    void aceptaPublicarConExactamente3HorasDeAnticipacion() {
        servicio.publicar(USUARIO_ID, form("1", "2026-11-16", "12:00", "13:00", "ONLINE", "", "5"));

        assertEquals(1, tutoriaRepo.guardadas.size());
    }

    @Test
    void rechazaUnaFechaPasada() {
        TutoriaValidacionException error = rechazada(form("1", "2026-11-10", "10:00", "11:00", "ONLINE", "", "5"));

        assertEquals("fecha", error.getCampo());
    }

    @Test
    void rechazaUnaAsignaturaQueElTutorNoTieneCertificada() {
        TutoriaValidacionException error = rechazada(form("99", MANANA, "10:00", "11:00", "ONLINE", "", "5"));

        assertEquals("asignaturaId", error.getCampo());
        assertTrue(tutoriaRepo.guardadas.isEmpty());
    }

    @Test
    void rechazaUnaFranjaQueSeCruzaConOtraTutoriaDelTutor() {
        tutoriaRepo.conflictoTutor = true;

        TutoriaValidacionException error = rechazada(form("1", MANANA, "10:00", "11:00", "ONLINE", "", "5"));

        assertEquals("horaInicio", error.getCampo());
        assertTrue(tutoriaRepo.guardadas.isEmpty());
    }

    @Test
    void informaTodosLosCamposInvalidosALaVez() {
        TutoriaValidacionException error = rechazada(form("", "", "", "", "FISICA", "", "0"));

        assertTrue(error.getErrores().containsKey("asignaturaId"));
        assertTrue(error.getErrores().containsKey("fecha"));
        assertTrue(error.getErrores().containsKey("horaInicio"));
        assertTrue(error.getErrores().containsKey("horaFin"));
        assertTrue(error.getErrores().containsKey("modalidad"));
        assertTrue(error.getErrores().containsKey("capacidad"));
        assertTrue(tutoriaRepo.guardadas.isEmpty());
    }

    @Test
    void exigeSalaEnUnaTutoriaPresencial() {
        TutoriaValidacionException error = rechazada(form("1", MANANA, "10:00", "11:00", "PRESENCIAL", "  ", "5"));

        assertEquals("sala", error.getCampo());
    }

    @Test
    void rechazaUnaCapacidadNoPositiva() {
        TutoriaValidacionException error = rechazada(form("1", MANANA, "10:00", "11:00", "ONLINE", "", "-3"));

        assertEquals("capacidad", error.getCampo());
    }

    @Test
    void unUsuarioSinPerfilDeTutorNoPuedePublicar() {
        tutorRepo.tutor = null;

        TutoriaValidacionException error = rechazada(form("1", MANANA, "10:00", "11:00", "ONLINE", "", "5"));

        assertNull(error.getCampo());
        assertTrue(error.getErrores().isEmpty());
        assertTrue(tutoriaRepo.guardadas.isEmpty());
    }

    @Test
    void traduceLaViolacionDelIndiceUnicoAUnMensajeDeNegocio() {
        tutoriaRepo.errorAlGuardar = new RuntimeException("no se pudo guardar",
                new SQLException("duplicate key value violates unique constraint", "23505"));

        TutoriaValidacionException error = rechazada(form("1", MANANA, "10:00", "11:00", "ONLINE", "", "5"));

        assertEquals("horaInicio", error.getCampo());
    }

    @Test
    void noOcultaOtrosErroresInesperados() {
        tutoriaRepo.errorAlGuardar = new IllegalStateException("fallo de conexión");

        assertThrows(IllegalStateException.class,
                () -> servicio.publicar(USUARIO_ID, form("1", MANANA, "10:00", "11:00", "ONLINE", "", "5")));
    }

    private TutoriaValidacionException rechazada(TutoriaForm form) {
        return assertThrows(TutoriaValidacionException.class, () -> servicio.publicar(USUARIO_ID, form));
    }

    private TutoriaForm form(String asignatura, String fecha, String inicio, String fin,
                             String modalidad, String sala, String capacidad) {
        return new TutoriaForm(asignatura, fecha, inicio, fin, modalidad, sala, capacidad);
    }
}
