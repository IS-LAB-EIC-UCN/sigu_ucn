package cl.ucn.app.repository;

import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.model.Equipo;
import cl.ucn.app.model.EstadoEquipo;
import cl.ucn.app.model.EstadoPrestamo;
import cl.ucn.app.model.Prestamo;
import cl.ucn.app.model.Producto;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.util.FechasInventario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.util.List;

public class JpaPrestamoRepository extends JpaRepositorioBase implements PrestamoRepository {

    /** JOIN FETCH para poder mostrar los datos fuera del EntityManager (relaciones LAZY). */
    private static final String SELECT_PRESTAMOS =
            "SELECT p FROM Prestamo p JOIN FETCH p.equipo JOIN FETCH p.usuario LEFT JOIN FETCH p.devueltoPor ";

    @Override
    public Prestamo registrarPrestamo(Long equipoId, Long usuarioId) {
        return enTransaccion(em -> {
            // Bloquea la fila del equipo: dos solicitudes simultáneas no pueden prestarlo a la vez.
            Producto producto = equipoId == null ? null
                    : em.find(Producto.class, equipoId, LockModeType.PESSIMISTIC_WRITE);
            if (!(producto instanceof Equipo equipo)) {
                throw new RecursoNoEncontradoException("El equipo seleccionado no existe.");
            }
            Usuario solicitante = usuarioActivo(em, usuarioId);

            Prestamo prestamo = Prestamo.iniciar(equipo, solicitante, FechasInventario.ahoraUtc());
            em.persist(prestamo);
            return prestamo;
        });
    }

    @Override
    public Prestamo registrarDevolucion(Long prestamoId, Long receptorId) {
        return enTransaccion(em -> {
            Prestamo prestamo = em.createQuery(SELECT_PRESTAMOS + "WHERE p.id = :id", Prestamo.class)
                    .setParameter("id", prestamoId)
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el préstamo indicado."));

            // Orden de bloqueo: primero el equipo y luego el préstamo; se relee el estado ya bloqueado.
            em.refresh(prestamo.getEquipo(), LockModeType.PESSIMISTIC_WRITE);
            em.refresh(prestamo, LockModeType.PESSIMISTIC_WRITE);

            Usuario receptor = usuarioActivo(em, receptorId);
            prestamo.finalizar(receptor, FechasInventario.ahoraUtc());
            return prestamo;
        });
    }

    @Override
    public List<Equipo> listarEquiposDisponibles() {
        return leer(em -> em.createQuery(
                        "SELECT e FROM Equipo e JOIN FETCH e.categoria "
                                + "WHERE e.activo = true AND e.estado = :estado ORDER BY e.codigo", Equipo.class)
                .setParameter("estado", EstadoEquipo.DISPONIBLE)
                .getResultList());
    }

    @Override
    public List<Prestamo> listarActivos() {
        return leer(em -> em.createQuery(
                        SELECT_PRESTAMOS + "WHERE p.estado = :estado ORDER BY p.fechaPrestamo", Prestamo.class)
                .setParameter("estado", EstadoPrestamo.ACTIVO)
                .getResultList());
    }

    @Override
    public List<Prestamo> listarPorUsuario(Long usuarioId) {
        return leer(em -> em.createQuery(
                        SELECT_PRESTAMOS + "WHERE p.usuario.id = :usuarioId ORDER BY p.fechaPrestamo DESC",
                        Prestamo.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList());
    }

    private static Usuario usuarioActivo(EntityManager em, Long usuarioId) {
        Usuario usuario = usuarioId == null ? null : em.find(Usuario.class, usuarioId);
        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RecursoNoEncontradoException("El usuario de la sesión no es válido o está inactivo.");
        }
        return usuario;
    }
}
