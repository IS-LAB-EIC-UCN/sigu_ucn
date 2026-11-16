package cl.ucn.app.model;

import cl.ucn.app.exception.EquipoNoDisponibleException;
import cl.ucn.app.exception.ReglaNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de las reglas de CU03 y CU04 en la entidad Prestamo (Grupo 5).
 * No requieren base de datos.
 */
class PrestamoTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 11, 10, 10, 0);

    private Equipo equipo;
    private Usuario solicitante;
    private Usuario encargado;

    @BeforeEach
    void setUp() {
        equipo = new Equipo("EQ-010", "Microscopio", null, new Categoria("Microscopía"), null);
        solicitante = new Usuario("Estudiante", "est@sigu.cl", "x", true, null);
        encargado = new Usuario("Encargado", "enc@sigu.cl", "x", true, null);
    }

    @Test
    @DisplayName("Iniciar un préstamo lo deja ACTIVO y el equipo PRESTADO")
    void iniciarPrestamo() {
        Prestamo prestamo = Prestamo.iniciar(equipo, solicitante, INICIO);

        assertEquals(EstadoPrestamo.ACTIVO, prestamo.getEstado());
        assertEquals(INICIO, prestamo.getFechaPrestamo());
        assertNull(prestamo.getFechaDevolucion());
        assertEquals(EstadoEquipo.PRESTADO, equipo.getEstado());
    }

    @Test
    @DisplayName("No se puede prestar un equipo que ya está prestado")
    void rechazaEquipoPrestado() {
        Prestamo.iniciar(equipo, solicitante, INICIO);

        assertThrows(EquipoNoDisponibleException.class,
                () -> Prestamo.iniciar(equipo, solicitante, INICIO.plusMinutes(5)));
    }

    @Test
    @DisplayName("No se puede prestar un equipo dado de baja")
    void rechazaEquipoInactivo() {
        equipo.setActivo(false);

        assertThrows(EquipoNoDisponibleException.class,
                () -> Prestamo.iniciar(equipo, solicitante, INICIO));
    }

    @Test
    @DisplayName("Finalizar registra fecha y receptor, y deja el equipo DISPONIBLE")
    void finalizarPrestamo() {
        Prestamo prestamo = Prestamo.iniciar(equipo, solicitante, INICIO);
        LocalDateTime devolucion = INICIO.plusHours(2);

        prestamo.finalizar(encargado, devolucion);

        assertEquals(EstadoPrestamo.FINALIZADO, prestamo.getEstado());
        assertEquals(devolucion, prestamo.getFechaDevolucion());
        assertSame(encargado, prestamo.getDevueltoPor());
        assertEquals(EstadoEquipo.DISPONIBLE, equipo.getEstado());
    }

    @Test
    @DisplayName("No se puede registrar dos veces la devolución de un préstamo")
    void rechazaDevolucionDuplicada() {
        Prestamo prestamo = Prestamo.iniciar(equipo, solicitante, INICIO);
        prestamo.finalizar(encargado, INICIO.plusHours(1));

        assertThrows(ReglaNegocioException.class,
                () -> prestamo.finalizar(encargado, INICIO.plusHours(2)));
    }

    @Test
    @DisplayName("La devolución no puede ser anterior al préstamo")
    void rechazaDevolucionAnterior() {
        Prestamo prestamo = Prestamo.iniciar(equipo, solicitante, INICIO);

        assertThrows(ReglaNegocioException.class,
                () -> prestamo.finalizar(encargado, INICIO.minusMinutes(1)));
        assertEquals(EstadoPrestamo.ACTIVO, prestamo.getEstado());
        assertEquals(EstadoEquipo.PRESTADO, equipo.getEstado());
    }
}
