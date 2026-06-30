package cl.ucn.app.service;

import cl.ucn.app.model.*;
import cl.ucn.app.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutoriaServiceTest {

    @Mock private TutoriaRepository tutoriaRepository;
    @Mock private TutoriaReservaRepository reservaRepository;
    @Mock private AsignaturaRepository asignaturaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    private TutoriaService service;

    @BeforeEach
    void setUp() {
        service = new TutoriaService(
                tutoriaRepository,
                reservaRepository,
                asignaturaRepository,
                usuarioRepository
        );
    }

    // ─── crearAsignatura ────────────────────────────────────────────────────

    @Test
    @DisplayName("crearAsignatura: lanza excepción si el código es nulo")
    void crearAsignatura_codigoNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.crearAsignatura(null, "Cálculo I")
        );
        assertEquals("El código de la asignatura es obligatorio.", ex.getMessage());
    }

    @Test
    @DisplayName("crearAsignatura: lanza excepción si el código ya existe")
    void crearAsignatura_codigoDuplicado_lanzaExcepcion() {
        when(asignaturaRepository.findByCodigo("MAT101"))
                .thenReturn(new Asignatura("MAT101", "Cálculo I"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.crearAsignatura("MAT101", "Otro nombre")
        );
        assertEquals("Ya existe una asignatura con ese código.", ex.getMessage());
    }

    @Test
    @DisplayName("crearAsignatura: guarda correctamente cuando los datos son válidos")
    void crearAsignatura_datosValidos_guardaAsignatura() {
        when(asignaturaRepository.findByCodigo("MAT101")).thenReturn(null);

        service.crearAsignatura("MAT101", "Cálculo I");

        verify(asignaturaRepository, times(1)).save(any(Asignatura.class));
    }

    // ─── publicarDisponibilidad ──────────────────────────────────────────────

    @Test
    @DisplayName("publicarDisponibilidad: lanza excepción si la fecha es pasada")
    void publicarDisponibilidad_fechaPasada_lanzaExcepcion() {
        LocalDate ayer = LocalDate.now().minusDays(1);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.publicarDisponibilidad(1L, 1L, ayer,
                        LocalTime.of(10, 0), LocalTime.of(11, 0))
        );
        assertEquals("No se puede publicar una tutoría en fecha pasada.", ex.getMessage());
    }

    @Test
    @DisplayName("publicarDisponibilidad: lanza excepción si el tutor no existe")
    void publicarDisponibilidad_tutorNoExiste_lanzaExcepcion() {
        LocalDate manana = LocalDate.now().plusDays(1);
        when(usuarioRepository.findById(99L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.publicarDisponibilidad(99L, 1L, manana,
                        LocalTime.of(10, 0), LocalTime.of(11, 0))
        );
        assertEquals("El tutor no existe.", ex.getMessage());
    }

    @Test
    @DisplayName("publicarDisponibilidad: guarda la tutoría cuando los datos son válidos")
    void publicarDisponibilidad_datosValidos_guardaTutoria() {
        LocalDate manana = LocalDate.now().plusDays(1);
        Usuario tutor = new Usuario("Pedro", "pedro@ucn.cl", "pass", true, new Rol("TUTOR"));
        Asignatura asig = new Asignatura("MAT101", "Cálculo I");

        when(usuarioRepository.findById(1L)).thenReturn(tutor);
        when(asignaturaRepository.findById(1L)).thenReturn(asig);

        service.publicarDisponibilidad(1L, 1L, manana,
                LocalTime.of(10, 0), LocalTime.of(11, 0));

        verify(tutoriaRepository, times(1)).save(any(Tutoria.class));
    }

    // ─── reservarTutoria ────────────────────────────────────────────────────

    @Test
    @DisplayName("reservarTutoria: lanza excepción si la tutoría no está disponible")
    void reservarTutoria_tutoriaNoDisponible_lanzaExcepcion() {
        Tutoria tutoria = new Tutoria(1L, null, null,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "RESERVADA");
        when(tutoriaRepository.findById(1L)).thenReturn(tutoria);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.reservarTutoria(1L, 1L)
        );
        assertEquals("La tutoría no está disponible.", ex.getMessage());
    }

    @Test
    @DisplayName("reservarTutoria: lanza excepción si la franja ya fue reservada")
    void reservarTutoria_franjaYaReservada_lanzaExcepcion() {
        Tutoria tutoria = new Tutoria(1L, null, null,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "DISPONIBLE");
        Usuario estudiante = new Usuario("Ana", "ana@ucn.cl", "pass", true, new Rol("ESTUDIANTE"));

        when(tutoriaRepository.findById(1L)).thenReturn(tutoria);
        when(usuarioRepository.findById(1L)).thenReturn(estudiante);
        when(reservaRepository.findByTutoriaId(1L)).thenReturn(new TutoriaReserva());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.reservarTutoria(1L, 1L)
        );
        assertEquals("Esta franja horaria ya fue reservada.", ex.getMessage());
    }

    @Test
    @DisplayName("reservarTutoria: crea reserva y cambia estado a RESERVADA cuando todo es válido")
    void reservarTutoria_datosValidos_creaReservaYCambiaEstado() {
        Tutoria tutoria = new Tutoria(1L, null, null,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "DISPONIBLE");
        Usuario estudiante = new Usuario("Ana", "ana@ucn.cl", "pass", true, new Rol("ESTUDIANTE"));

        when(tutoriaRepository.findById(1L)).thenReturn(tutoria);
        when(usuarioRepository.findById(1L)).thenReturn(estudiante);
        when(reservaRepository.findByTutoriaId(1L)).thenReturn(null);

        service.reservarTutoria(1L, 1L);

        verify(reservaRepository, times(1)).save(any(TutoriaReserva.class));
        assertEquals("RESERVADA", tutoria.getEstado());
        verify(tutoriaRepository, times(1)).update(tutoria);
    }

    // ─── registrarAsistencia ────────────────────────────────────────────────

    @Test
    @DisplayName("registrarAsistencia: marca asistencia y cambia tutoría a REALIZADA")
    void registrarAsistencia_marcaAsistioYCambiaEstado() {
        Tutoria tutoria = new Tutoria(1L, null, null,
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "RESERVADA");
        TutoriaReserva reserva = new TutoriaReserva(1L, tutoria, null, null, null);
        when(reservaRepository.findById(1L)).thenReturn(reserva);

        service.registrarAsistencia(1L, true);

        assertTrue(reserva.getAsistio());
        assertEquals("REALIZADA", tutoria.getEstado());
        verify(reservaRepository, times(1)).update(reserva);
        verify(tutoriaRepository, times(1)).update(tutoria);
    }
}