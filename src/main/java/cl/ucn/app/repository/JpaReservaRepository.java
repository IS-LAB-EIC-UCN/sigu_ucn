package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JpaReservaRepository implements ReservaRepository {
    @Override
    public void save(Reserva reserva) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(reserva);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reserva r " +
                "JOIN FETCH r.usuario u " +
                "JOIN FETCH u.rol " +
                "JOIN FETCH r.espacio " +
                "WHERE r.id = :id", Reserva.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return findWithFilters(usuarioId, null, null, null, null);
    }

    @Override
    public List<Reserva> findByEspacioId(Long espacioId) {
        return findWithFilters(null, espacioId, null, null, null);
    }

    @Override
    public List<Reserva> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT r FROM Reserva r " +
                "JOIN FETCH r.usuario u " +
                "JOIN FETCH u.rol " +
                "JOIN FETCH r.espacio", Reserva.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reserva> findWithFilters(Long usuarioId, Long espacioId, String estado, LocalDateTime desde, LocalDateTime hasta) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Reserva> cq = cb.createQuery(Reserva.class);
            Root<Reserva> reserva = cq.from(Reserva.class);
            
            // Eager fetch to avoid LazyInitializationException in Jackson mapping
            reserva.fetch("usuario", JoinType.LEFT).fetch("rol", JoinType.LEFT);
            reserva.fetch("espacio", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            if (usuarioId != null) {
                predicates.add(cb.equal(reserva.get("usuario").get("id"), usuarioId));
            }
            if (espacioId != null) {
                predicates.add(cb.equal(reserva.get("espacio").get("id"), espacioId));
            }
            if (estado != null && !estado.isEmpty()) {
                predicates.add(cb.equal(reserva.get("estado"), estado));
            }
            if (desde != null) {
                predicates.add(cb.greaterThanOrEqualTo(reserva.get("fechaReserva"), desde.toLocalDate()));
            }
            if (hasta != null) {
                predicates.add(cb.lessThanOrEqualTo(reserva.get("fechaReserva"), hasta.toLocalDate()));
            }

            cq.where(predicates.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean hasOverlap(Long espacioId, LocalDate fechaReserva, LocalTime horaInicio, LocalTime horaFin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(r) FROM Reserva r " +
                "WHERE r.espacio.id = :espacioId " +
                "AND r.estado = :estado " +
                "AND r.fechaReserva = :fechaReserva " +
                "AND r.horaInicio < :horaFin " +
                "AND r.horaFin > :horaInicio", Long.class)
                .setParameter("espacioId", espacioId)
                .setParameter("estado", "APROBADA")
                .setParameter("fechaReserva", fechaReserva)
                .setParameter("horaInicio", horaInicio)
                .setParameter("horaFin", horaFin)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
