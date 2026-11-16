package cl.ucn.app.repository;

import cl.ucn.app.model.Equipo;
import cl.ucn.app.model.Prestamo;

import java.util.List;

/**
 * Acceso a los préstamos de equipos (Grupo 5).
 * Las operaciones de escritura son atómicas: el préstamo y el estado del equipo
 * se confirman o se revierten juntos.
 */
public interface PrestamoRepository {

    /**
     * Crea un préstamo ACTIVO y marca el equipo como PRESTADO en una sola transacción.
     *
     * @throws cl.ucn.app.exception.RecursoNoEncontradoException si el equipo o el usuario no existen
     * @throws cl.ucn.app.exception.EquipoNoDisponibleException  si el equipo ya está prestado
     */
    Prestamo registrarPrestamo(Long equipoId, Long usuarioId);

    /**
     * Finaliza el préstamo y deja el equipo DISPONIBLE en una sola transacción.
     *
     * @throws cl.ucn.app.exception.RecursoNoEncontradoException si el préstamo o el receptor no existen
     * @throws cl.ucn.app.exception.ReglaNegocioException        si el préstamo ya estaba finalizado
     */
    Prestamo registrarDevolucion(Long prestamoId, Long receptorId);

    /** Equipos activos en estado DISPONIBLE. */
    List<Equipo> listarEquiposDisponibles();

    /** Préstamos en estado ACTIVO, del más antiguo al más reciente. */
    List<Prestamo> listarActivos();

    /** Préstamos de un usuario (activos y finalizados), del más reciente al más antiguo. */
    List<Prestamo> listarPorUsuario(Long usuarioId);
}
