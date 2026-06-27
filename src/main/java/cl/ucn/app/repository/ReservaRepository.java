package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Reserva;
import jakarta.persistence.EntityManager;
import java.util.Optional;

import java.util.List;

public class ReservaRepository {

    public List<Reserva> findByUsuarioId(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM Reserva r " +
                            "JOIN FETCH r.espacio " +
                            "JOIN FETCH r.vehiculo " +
                            "WHERE r.usuario.id = :usuarioId",
                    Reserva.class
            ).setParameter("usuarioId", usuarioId).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reserva> findReservasActivas() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT r FROM Reserva r " +
                            "JOIN FETCH r.espacio " +
                            "WHERE r.estado IN ('PENDIENTE', 'APROBADA')",
                    Reserva.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Reserva> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            Reserva reserva = em.find(Reserva.class, id);
            return Optional.ofNullable(reserva);

        } finally {
            em.close();
        }
    }

    public Reserva save(Reserva reserva) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            if (reserva.getId() == null) {
                em.persist(reserva);
            } else {
                reserva = em.merge(reserva);
            }

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