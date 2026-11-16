package cl.ucn.app.service;

import cl.ucn.app.exception.EquipoNoDisponibleException;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.Categoria;
import cl.ucn.app.model.Equipo;
import cl.ucn.app.model.Prestamo;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.PrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de CU03 - Solicitar préstamo y CU04 - Registrar devolución (Grupo 5).
 * El repositorio se simula con Mockito: no se necesita base de datos.
 */
@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    private PrestamoService service;
    private Prestamo prestamo;

    @BeforeEach
    void setUp() {
        service = new PrestamoService(prestamoRepository);
        Equipo equipo = new Equipo("EQ-010", "Microscopio", null, new Categoria("Microscopía"), null);
        Usuario solicitante = new Usuario("Estudiante", "est@sigu.cl", "x", true, null);
        prestamo = Prestamo.iniciar(equipo, solicitante, LocalDateTime.of(2026, 11, 10, 10, 0));
    }

    @Test
    @DisplayName("Solicitar préstamo delega en el repositorio y devuelve el préstamo")
    void solicitaPrestamo() {
        when(prestamoRepository.registrarPrestamo(1L, 7L)).thenReturn(prestamo);

        Prestamo resultado = service.solicitarPrestamo(1L, 7L);

        assertSame(prestamo, resultado);
        verify(prestamoRepository).registrarPrestamo(1L, 7L);
    }

    @Test
    @DisplayName("Rechaza la solicitud sin equipo seleccionado")
    void rechazaSinEquipo() {
        assertThrows(ReglaNegocioException.class, () -> service.solicitarPrestamo(null, 7L));
        verifyNoInteractions(prestamoRepository);
    }

    @Test
    @DisplayName("Propaga el error cuando el equipo no está disponible")
    void propagaEquipoNoDisponible() {
        when(prestamoRepository.registrarPrestamo(1L, 7L))
                .thenThrow(new EquipoNoDisponibleException("EQ-010"));

        assertThrows(EquipoNoDisponibleException.class, () -> service.solicitarPrestamo(1L, 7L));
    }

    @Test
    @DisplayName("Propaga el error cuando el equipo no existe")
    void propagaEquipoInexistente() {
        when(prestamoRepository.registrarPrestamo(99L, 7L))
                .thenThrow(new RecursoNoEncontradoException("El equipo seleccionado no existe."));

        assertThrows(RecursoNoEncontradoException.class, () -> service.solicitarPrestamo(99L, 7L));
    }

    @Test
    @DisplayName("Registrar devolución delega en el repositorio")
    void registraDevolucion() {
        when(prestamoRepository.registrarDevolucion(5L, 3L)).thenReturn(prestamo);

        Prestamo resultado = service.registrarDevolucion(5L, 3L);

        assertSame(prestamo, resultado);
        verify(prestamoRepository).registrarDevolucion(5L, 3L);
    }

    @Test
    @DisplayName("Rechaza la devolución sin préstamo seleccionado")
    void rechazaDevolucionSinPrestamo() {
        assertThrows(ReglaNegocioException.class, () -> service.registrarDevolucion(null, 3L));
        verifyNoInteractions(prestamoRepository);
    }

    @Test
    @DisplayName("Propaga el error cuando la devolución ya estaba registrada")
    void propagaDevolucionDuplicada() {
        when(prestamoRepository.registrarDevolucion(5L, 3L))
                .thenThrow(new ReglaNegocioException("La devolución de este préstamo ya fue registrada."));

        assertThrows(ReglaNegocioException.class, () -> service.registrarDevolucion(5L, 3L));
    }
}
