package cl.ucn.app.service;

import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.Equipo;
import cl.ucn.app.model.Prestamo;
import cl.ucn.app.repository.PrestamoRepository;

import java.util.List;

/**
 * CU03 - Solicitar préstamo de equipo y CU04 - Registrar devolución (Grupo 5).
 * Depende solo de la interfaz del repositorio (se inyecta por constructor).
 * Las reglas del préstamo viven en las entidades {@code Prestamo} y {@code Equipo};
 * el repositorio garantiza la atomicidad de la operación.
 */
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;

    public PrestamoService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    /** CU03: equipos que se pueden solicitar. */
    public List<Equipo> equiposDisponibles() {
        return prestamoRepository.listarEquiposDisponibles();
    }

    /** CU03: estado de las solicitudes del usuario. */
    public List<Prestamo> prestamosDeUsuario(Long usuarioId) {
        return prestamoRepository.listarPorUsuario(usuarioId);
    }

    /** CU04: préstamos pendientes de devolución. */
    public List<Prestamo> prestamosActivos() {
        return prestamoRepository.listarActivos();
    }

    /**
     * CU03: registra el préstamo del equipo al usuario de la sesión.
     */
    public Prestamo solicitarPrestamo(Long equipoId, Long usuarioId) {
        if (equipoId == null) {
            throw new ReglaNegocioException("Debe seleccionar un equipo.");
        }
        if (usuarioId == null) {
            throw new ReglaNegocioException("Debe iniciar sesión para solicitar un préstamo.");
        }
        return prestamoRepository.registrarPrestamo(equipoId, usuarioId);
    }

    /**
     * CU04: registra la devolución; el receptor es el usuario de la sesión.
     */
    public Prestamo registrarDevolucion(Long prestamoId, Long receptorId) {
        if (prestamoId == null) {
            throw new ReglaNegocioException("Debe seleccionar un préstamo.");
        }
        if (receptorId == null) {
            throw new ReglaNegocioException("Debe iniciar sesión para registrar una devolución.");
        }
        return prestamoRepository.registrarDevolucion(prestamoId, receptorId);
    }
}
