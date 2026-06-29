package cl.ucn.app.integration;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.service.EstacionamientoService;
import cl.ucn.app.service.RegistroEstacionamientoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class EstacionamientoIntegrationTest {

    private final EstacionamientoService estacionamientoService = new EstacionamientoService();
    private final RegistroEstacionamientoService registroService = new RegistroEstacionamientoService();

    @Test
    void debeCrearReservaPendiente() {
        LocalDate fecha = LocalDate.now().plusDays(30);

        estacionamientoService.reservarEspacio(
                2L, 5L, 1L, 5,
                fecha,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        Reserva reserva = buscarReserva(2L, 5L, 5, fecha);

        assertNotNull(reserva);
        assertEquals("PENDIENTE", reserva.getEstado());
    }

    @Test
    void debeAprobarReservaPendiente() {
        LocalDate fecha = LocalDate.now().plusDays(31);

        estacionamientoService.reservarEspacio(
                2L, 5L, 1L, 6,
                fecha,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        Reserva reserva = buscarReserva(2L, 5L, 6, fecha);

        estacionamientoService.aprobarReserva(reserva.getId());

        Reserva reservaActualizada = buscarReservaPorId(reserva.getId());

        assertEquals("APROBADA", reservaActualizada.getEstado());
    }

    @Test
    void debeRegistrarIngresoYSalida() {
        LocalDate fecha = LocalDate.now();
        LocalTime inicio = LocalTime.now().minusMinutes(1);
        LocalTime fin = LocalTime.now().plusHours(1);

        Reserva reserva = crearReservaAprobadaDirecta(2L, 5L, 1L, 7, fecha, inicio, fin);

        registroService.registrarIngreso(reserva.getId());

        Reserva activa = buscarReservaPorId(reserva.getId());
        assertEquals("ACTIVA", activa.getEstado());

        Long registroId = buscarRegistroIdPorReserva(reserva.getId());

        registroService.registrarSalida(registroId);

        Reserva finalizada = buscarReservaPorId(reserva.getId());
        assertEquals("FINALIZADA", finalizada.getEstado());
    }

    private Reserva buscarReserva(Long usuarioId, Long espacioId, Integer puesto, LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT r FROM Reserva r " +
                                    "WHERE r.usuario.id = :usuarioId " +
                                    "AND r.espacio.id = :espacioId " +
                                    "AND r.puestoNumero = :puesto " +
                                    "AND r.fechaReserva = :fecha " +
                                    "ORDER BY r.id DESC",
                            Reserva.class
                    )
                    .setParameter("usuarioId", usuarioId)
                    .setParameter("espacioId", espacioId)
                    .setParameter("puesto", puesto)
                    .setParameter("fecha", fecha)
                    .setMaxResults(1)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    private Reserva buscarReservaPorId(Long reservaId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Reserva.class, reservaId);
        } finally {
            em.close();
        }
    }

    private Long buscarRegistroIdPorReserva(Long reservaId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                            "SELECT r.id FROM RegistroEstacionamiento r WHERE r.reserva.id = :reservaId",
                            Long.class
                    )
                    .setParameter("reservaId", reservaId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    private Reserva crearReservaAprobadaDirecta(Long usuarioId, Long espacioId, Long vehiculoId,
                                                Integer puesto, LocalDate fecha,
                                                LocalTime inicio, LocalTime fin) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            var usuario = em.find(cl.ucn.app.model.Usuario.class, usuarioId);
            var espacio = em.find(cl.ucn.app.model.Espacio.class, espacioId);
            var vehiculo = em.find(cl.ucn.app.model.Vehiculo.class, vehiculoId);

            Reserva reserva = new Reserva(
                    fecha,
                    inicio,
                    fin,
                    "APROBADA",
                    puesto,
                    usuario,
                    espacio,
                    vehiculo
            );

            em.persist(reserva);
            em.getTransaction().commit();

            return reserva;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}