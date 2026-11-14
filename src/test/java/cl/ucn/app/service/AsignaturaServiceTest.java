package cl.ucn.app.service;

import cl.ucn.app.dto.AsignaturaForm;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.repository.AsignaturaRepository;
import cl.ucn.app.repository.TutoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignaturaServiceTest {

    private static final String CARRERA = "Ingeniería Civil en Computación e Informática";

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Mock
    private TutoriaRepository tutoriaRepository;

    private AsignaturaService asignaturaService;

    @BeforeEach
    void setUp() {
        asignaturaService = new AsignaturaService(asignaturaRepository, tutoriaRepository);
    }

    @Test
    void crearGuardaLaAsignaturaConElCodigoNormalizado() {
        when(asignaturaRepository.findByCodigo("DCCB-103")).thenReturn(Optional.empty());

        Asignatura creada = asignaturaService.crear(new AsignaturaForm(" dccb-103 ", " Cálculo II ", CARRERA, "2"));

        assertEquals("DCCB-103", creada.getCodigo());
        assertEquals("Cálculo II", creada.getNombre());
        assertEquals(2, creada.getSemestre());
        verify(asignaturaRepository).save(creada);
    }

    @Test
    void crearRechazaUnCodigoQueYaExiste() {
        when(asignaturaRepository.findByCodigo("DCCB-101")).thenReturn(Optional.of(asignatura(1L, "DCCB-101")));

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> asignaturaService.crear(new AsignaturaForm("DCCB-101", "Cálculo I", CARRERA, "1")));

        assertEquals("codigo", error.getCampo());
        verify(asignaturaRepository, never()).save(any());
    }

    @Test
    void crearRechazaCamposObligatoriosVacios() {
        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> asignaturaService.crear(new AsignaturaForm("EIC-101", "   ", CARRERA, "1")));

        assertEquals("nombre", error.getCampo());
        verifyNoInteractions(asignaturaRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "13", "dos", ""})
    void crearRechazaSemestresInvalidos(String semestre) {
        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> asignaturaService.crear(new AsignaturaForm("EIC-101", "Programación", CARRERA, semestre)));

        assertEquals("semestre", error.getCampo());
        verifyNoInteractions(asignaturaRepository);
    }

    @Test
    void actualizarPermiteConservarElMismoCodigo() {
        Asignatura existente = asignatura(1L, "DCCB-101");
        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(asignaturaRepository.findByCodigo("DCCB-101")).thenReturn(Optional.of(existente));

        asignaturaService.actualizar(1L, new AsignaturaForm("DCCB-101", "Cálculo I Avanzado", CARRERA, "1"));

        assertEquals("Cálculo I Avanzado", existente.getNombre());
        verify(asignaturaRepository).update(existente);
    }

    @Test
    void actualizarRechazaElCodigoDeOtraAsignatura() {
        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignatura(1L, "DCCB-101")));
        when(asignaturaRepository.findByCodigo("DCCB-102")).thenReturn(Optional.of(asignatura(2L, "DCCB-102")));

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> asignaturaService.actualizar(1L, new AsignaturaForm("DCCB-102", "Cálculo I", CARRERA, "1")));

        assertEquals("codigo", error.getCampo());
        verify(asignaturaRepository, never()).update(any());
    }

    @Test
    void eliminarRechazaAsignaturaConTutoriasActivas() {
        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignatura(1L, "DCCB-101")));
        when(tutoriaRepository.countByAsignaturaAndEstado(1L, EstadoTutoria.PUBLICADA)).thenReturn(2L);

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> asignaturaService.eliminar(1L));

        assertTrue(error.getMessage().contains("tutorías activas"));
        verify(asignaturaRepository, never()).delete(any());
    }

    @Test
    void eliminarRechazaAsignaturaConHistorialDeTutorias() {
        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignatura(1L, "DCCB-101")));
        when(tutoriaRepository.countByAsignaturaAndEstado(1L, EstadoTutoria.PUBLICADA)).thenReturn(0L);
        when(tutoriaRepository.countByAsignatura(1L)).thenReturn(4L);

        TutoriaValidacionException error = assertThrows(TutoriaValidacionException.class,
                () -> asignaturaService.eliminar(1L));

        assertTrue(error.getMessage().contains("historial"));
        verify(asignaturaRepository, never()).delete(any());
    }

    @Test
    void eliminarBorraLaAsignaturaSinTutorias() {
        when(asignaturaRepository.findById(5L)).thenReturn(Optional.of(asignatura(5L, "EIC-601")));
        when(tutoriaRepository.countByAsignaturaAndEstado(5L, EstadoTutoria.PUBLICADA)).thenReturn(0L);
        when(tutoriaRepository.countByAsignatura(5L)).thenReturn(0L);

        asignaturaService.eliminar(5L);

        verify(asignaturaRepository).delete(5L);
    }

    private Asignatura asignatura(Long id, String codigo) {
        Asignatura asignatura = new Asignatura(codigo, "Asignatura de prueba", CARRERA, 1);
        try {
            Field campoId = Asignatura.class.getDeclaredField("id");
            campoId.setAccessible(true);
            campoId.set(asignatura, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return asignatura;
    }
}
