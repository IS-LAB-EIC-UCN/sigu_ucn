package cl.ucn.app.repository;

import cl.ucn.app.model.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JpaReservaRepository implements ReservaRepository {
    @Override
    public void save(Reserva reserva) {
        EntityManager em = JpaConfig.getEntityManagerFactory().createEntityManager();
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
    public Optional<Reserva> findById(String id) {
        EntityManager em = JpaConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(Reserva.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reserva> findByUsuarioId(String usuarioId) {
        return findWithFilters(usuarioId, null, null, null, null);
    }

    @Override
    public List<Reserva> findByEspacioId(String espacioId) {
        return findWithFilters(null, espacioId, null, null, null);
    }

    @Override
    public List<Reserva> findAll() {
        EntityManager em = JpaConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reserva r", Reserva.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reserva> findWithFilters(String usuarioId, String espacioId, String estado, LocalDateTime desde, LocalDateTime hasta) {
        EntityManager em = JpaConfig.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Reserva> cq = cb.createQuery(Reserva.class);
            Root<Reserva> reserva = cq.from(Reserva.class);
            List<Predicate> predicates = new ArrayList<>();

            if (usuarioId != null && !usuarioId.isEmpty()) {
                predicates.add(cb.equal(reserva.get("usuario").get("id"), usuarioId));
            }
            if (espacioId != null && !espacioId.isEmpty()) {
                predicates.add(cb.equal(reserva.get("espacio").get("id"), espacioId));
            }
            if (estado != null && !estado.isEmpty()) {
                predicates.add(cb.equal(reserva.get("estado"), Reserva.Estado.valueOf(estado)));
            }
            if (desde != null) {
                predicates.add(cb.greaterThanOrEqualTo(reserva.get("fechaInicio"), desde));
            }
            if (hasta != null) {
                predicates.add(cb.lessThanOrEqualTo(reserva.get("fechaFin"), hasta));
            }

            cq.where(predicates.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean hasOverlap(String espacioId, LocalDateTime inicio, LocalDateTime fin) {
        EntityManager em = JpaConfig.getEntityManagerFactory().createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(r) FROM Reserva r " +
                "WHERE r.espacio.id = :espacioId " +
                "AND r.estado = :estado " +
                "AND r.fechaInicio < :fin " +
                "AND r.fechaFin > :inicio", Long.class)
                .setParameter("espacioId", espacioId)
                .setParameter("estado", Reserva.Estado.APROBADA)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
