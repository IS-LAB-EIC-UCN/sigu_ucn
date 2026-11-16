package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.EstadoReserva;
import cl.ucn.app.model.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaRepository {
    public List<Reserva> buscarPorUsuario(Long usuarioId, Long espacioId, LocalDate fecha, String estado) {
        StringBuilder jpql = new StringBuilder("SELECT r FROM Reserva r JOIN FETCH r.espacio WHERE r.usuario.id = :usuarioId");
        if (espacioId != null) {
            jpql.append(" AND r.espacio.id = :espacioId");
        }
        if (fecha != null) {
            jpql.append(" AND r.fechaReserva = :fecha");
        }
        if (estado != null) {
            jpql.append(" AND r.estado = :estado");
        }
        jpql.append(" ORDER BY r.fechaReserva DESC, r.horaInicio DESC");
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Reserva> query = em.createQuery(jpql.toString(), Reserva.class);
            query.setParameter("usuarioId", usuarioId);
            if (espacioId != null) {
                query.setParameter("espacioId", espacioId);
            }
            if (fecha != null) {
                query.setParameter("fecha", fecha);
            }
            if (estado != null) {
                query.setParameter("estado", EstadoReserva.valueOf(estado));
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    public List<Reserva> buscarPendientes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM Reserva r JOIN FETCH r.usuario JOIN FETCH r.espacio " +
                    "WHERE r.estado = :estado ORDER BY r.fechaCreacion ASC, r.id ASC",
                    Reserva.class)
                    .setParameter("estado", EstadoReserva.PENDIENTE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Reserva buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM Reserva r JOIN FETCH r.espacio JOIN FETCH r.usuario WHERE r.id = :id",Reserva.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Reserva> buscarActivasPorEspacioYFecha(Long espacioId, LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM Reserva r WHERE r.espacio.id = :espacioId "
                            + "AND r.fechaReserva = :fecha "
                            + "AND r.estado IN :estados "
                            + "ORDER BY r.horaInicio ASC",
                    Reserva.class)
                    .setParameter("espacioId", espacioId)
                    .setParameter("fecha", fecha)
                    .setParameter("estados", List.of(EstadoReserva.PENDIENTE, EstadoReserva.APROBADA))
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Persiste los cambios de una reserva cuyo estado ya fue modificado
     * mediante sus transiciones (aprobar, rechazar, cancelar).
     */
    public void actualizar(Reserva reserva) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(reserva);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void guardar(Reserva reserva) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(reserva);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    //ESTE MÉTODO ESTÁ SUSTITUIDO ABAJO
    /*public boolean existeConflicto(Long espacioId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            Long cantidad = em.createQuery(
                "SELECT COUNT(r) FROM Reserva r " +
                "WHERE r.espacio.id = :espacioId " +
                "AND r.fechaReserva = :fecha " +
                "AND r.estado IN ('PENDIENTE', 'APROBADA') " +
                "AND r.horaInicio < :horaFin " +
                "AND r.horaFin > :horaInicio",
                Long.class
            )
            .setParameter("espacioId", espacioId)
            .setParameter("fecha", fecha)
            .setParameter("horaInicio", horaInicio)
            .setParameter("horaFin", horaFin)
            .getSingleResult();

            return cantidad > 0;
        } finally {
            em.close();
        } */


    
    public boolean existeConflicto(Long espacioId, LocalDate fecha,
                                LocalTime horaInicio, LocalTime horaFin) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            Long cantidad = em.createQuery(
                "SELECT COUNT(r) FROM Reserva r " +
                "WHERE r.espacio.id = :espacioId " +
                "AND r.fechaReserva = :fecha " +
                "AND r.estado IN (:pendiente, :aprobada) " +
                "AND r.horaInicio < :horaFin " +
                "AND r.horaFin > :horaInicio",
                Long.class
            )
            .setParameter("espacioId", espacioId)
            .setParameter("fecha", fecha)
            .setParameter("pendiente", EstadoReserva.PENDIENTE)
            .setParameter("aprobada", EstadoReserva.APROBADA)
            .setParameter("horaInicio", horaInicio)
            .setParameter("horaFin", horaFin)
            .getSingleResult();

            return cantidad > 0;
        } finally {
            em.close();
        }
    }


}   






