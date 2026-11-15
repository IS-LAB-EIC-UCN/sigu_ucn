package cl.ucn.app.service;

import cl.ucn.app.dto.TutorForm;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.Tutor;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.AsignaturaRepository;
import cl.ucn.app.repository.EstudianteRepository;
import cl.ucn.app.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    private static final String CORREO = "bruno.castro@alumnos.ucn.cl";
    private static final String CARRERA = "Ingeniería Civil en Computación e Informática";

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    private TutorService tutorService;

    @BeforeEach
    void setUp() {
        tutorService = new TutorService(tutorRepository, estudianteRepository, asignaturaRepository);
    }

    @Test
    void registrarGuardaAlTutorConSusAsignaturas() {
        Estudiante estudiante = estudiante(3L, true);
        when(estudianteRepository.findByCorreo(CORREO)).thenReturn(Optional.of(estudiante));
        when(tutorRepository.existsByEstudianteId(3L)).thenReturn(false);
        when(asignaturaRepository.findByIds(List.of(1L, 3L)))
                .thenReturn(List.of(asignatura("DCCB-101"), asignatura("EIC-201")));

        Tutor tutor = tutorService.registrar(new TutorForm(" Bruno.Castro@alumnos.ucn.cl ", List.of("1", "3", "1")));

        assertSame(estudiante, tutor.getEstudiante());
        assertEquals(2, tutor.getAsignaturasCertificadas().size());
        verify(tutorRepository).save(tutor);
    }

    @Test
    void registrarExigeAlMenosUnaAsignatura() {
        when(estudianteRepository.findByCorreo(CORREO)).thenReturn(Optional.of(estudiante(3L, true)));
        when(tutorRepository.existsByEstudianteId(3L)).thenReturn(false);

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> tutorService.registrar(new TutorForm(CORREO, List.of())));

        assertEquals("asignaturas", error.getCampo());
        verify(tutorRepository, never()).save(any());
    }

    @Test
    void registrarRechazaUnCorreoQueNoEstaEnElRegistroDeEstudiantes() {
        when(estudianteRepository.findByCorreo("nadie@alumnos.ucn.cl")).thenReturn(Optional.empty());

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> tutorService.registrar(new TutorForm("nadie@alumnos.ucn.cl", List.of("1"))));

        assertEquals("correo", error.getCampo());
        verifyNoInteractions(tutorRepository, asignaturaRepository);
    }

    @Test
    void registrarRechazaUnEstudianteSinCuenta() {
        when(estudianteRepository.findByCorreo(CORREO)).thenReturn(Optional.of(estudiante(5L, false)));

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> tutorService.registrar(new TutorForm(CORREO, List.of("1"))));

        assertEquals("correo", error.getCampo());
        verifyNoInteractions(tutorRepository, asignaturaRepository);
    }

    @Test
    void registrarRechazaUnEstudianteQueYaEsTutor() {
        when(estudianteRepository.findByCorreo(CORREO)).thenReturn(Optional.of(estudiante(3L, true)));
        when(tutorRepository.existsByEstudianteId(3L)).thenReturn(true);

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> tutorService.registrar(new TutorForm(CORREO, List.of("1"))));

        assertEquals("correo", error.getCampo());
        verify(tutorRepository, never()).save(any());
    }

    @Test
    void registrarRechazaUnaAsignaturaInexistente() {
        when(estudianteRepository.findByCorreo(CORREO)).thenReturn(Optional.of(estudiante(3L, true)));
        when(tutorRepository.existsByEstudianteId(3L)).thenReturn(false);
        when(asignaturaRepository.findByIds(List.of(1L, 99L))).thenReturn(List.of(asignatura("DCCB-101")));

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> tutorService.registrar(new TutorForm(CORREO, List.of("1", "99"))));

        assertEquals("asignaturas", error.getCampo());
        verify(tutorRepository, never()).save(any());
    }

    private Estudiante estudiante(Long id, boolean conCuenta) {
        Estudiante estudiante = new Estudiante(CORREO, "Bruno Castro", CARRERA, 1);
        if (conCuenta) {
            estudiante.setUsuario(new Usuario());
        }
        try {
            Field campoId = Estudiante.class.getDeclaredField("id");
            campoId.setAccessible(true);
            campoId.set(estudiante, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return estudiante;
    }

    private Asignatura asignatura(String codigo) {
        return new Asignatura(codigo, "Asignatura de prueba", CARRERA, 1);
    }
}
