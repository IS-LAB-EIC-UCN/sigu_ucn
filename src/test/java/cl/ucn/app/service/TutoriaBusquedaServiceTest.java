package cl.ucn.app.service;

import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.dto.TutoriaListado;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.repository.InscripcionTutoriaRepository;
import cl.ucn.app.repository.TutoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutoriaBusquedaServiceTest {

    @Mock
    private TutoriaRepository tutoriaRepository;

    @Mock
    private InscripcionTutoriaRepository inscripcionRepository;

    @Test
    void calculaCuposYExcluyeTutoriasSinCupos() {
        Tutoria disponible = tutoria(1L, 3);
        Tutoria llena = tutoria(2L, 2);
        when(tutoriaRepository.buscarDisponibles(any(), any()))
                .thenReturn(List.of(disponible, llena));
        when(inscripcionRepository.countActivasByTutoriaId(1L)).thenReturn(1L);
        when(inscripcionRepository.countActivasByTutoriaId(2L)).thenReturn(2L);

        List<TutoriaListado> resultado = new TutoriaBusquedaService(
                tutoriaRepository, inscripcionRepository)
                .buscar(new TutoriaFiltro(null, null, LocalDate.now()));

        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(0).cuposDisponibles());
    }

    @Test
    void reenviaFiltrosAlRepositorio() {
        TutoriaFiltro filtro = new TutoriaFiltro(4L, 8L, LocalDate.of(2026, 11, 16));
        when(tutoriaRepository.buscarDisponibles(eq(filtro), any())).thenReturn(List.of());

        new TutoriaBusquedaService(tutoriaRepository, inscripcionRepository).buscar(filtro);

        verify(tutoriaRepository).buscarDisponibles(eq(filtro), any());
    }

    private Tutoria tutoria(Long id, int capacidad) {
        Tutoria tutoria = new Tutoria(null,
                new Asignatura("MAT-101", "Matemática", "Ingeniería", 1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                null, null, capacidad);
        try {
            Field field = Tutoria.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(tutoria, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return tutoria;
    }
}
