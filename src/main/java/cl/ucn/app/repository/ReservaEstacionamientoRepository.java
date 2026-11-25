
package cl.ucn.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.ReservaEstacionamiento;
import cl.ucn.app.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class ReservaEstacionamientoRepository {

    private final Supplier<EntityManager> entityManagerSupplier;

    public ReservaEstacionamientoRepository() {
        this(JPAUtil::getEntityManager);
    }

    public ReservaEstacionamientoRepository(
            Supplier<EntityManager> entityManagerSupplier) {
        this.entityManagerSupplier = Objects.requireNonNull(entityManagerSupplier);
    }

    public void save(ReservaEstacionamiento reserva) {
        EntityManager em = entityManagerSupplier.get();

        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            em.persist(reserva);

            transaction.commit();

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public ReservaEstacionamiento findById(Long id) {
        EntityManager em = entityManagerSupplier.get();

        try {
            return em.createQuery(
                "SELECT r FROM ReservaEstacionamiento r " +
                "JOIN FETCH r.usuario " +
                "JOIN FETCH r.vehiculo " +
                "JOIN FETCH r.estacionamiento " +
                "WHERE r.id = :id",
                ReservaEstacionamiento.class
            )
            .setParameter("id", id)
            .getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<ReservaEstacionamiento> findByUsuarioId(Long usuarioId) {
        EntityManager em = entityManagerSupplier.get();

        try {
            return em.createQuery(
                "SELECT r FROM ReservaEstacionamiento r " +
                "JOIN FETCH r.vehiculo " +
                "JOIN FETCH r.estacionamiento " +
                "WHERE r.usuario.id = :usuarioId " +
                "ORDER BY r.fechaHoraInicio",
                ReservaEstacionamiento.class
            )
            .setParameter("usuarioId", usuarioId)
            .getResultList();

        } finally {
            em.close();
        }
    }

    public boolean existsOverlappingReservation(
        Long estacionamientoId,
        LocalDateTime inicio,
        LocalDateTime fin
    ) {
        EntityManager em = entityManagerSupplier.get();

        try {
            Long cantidad = em.createQuery(
                "SELECT COUNT(r) FROM ReservaEstacionamiento r " +
                "WHERE r.estacionamiento.id = :estacionamientoId " +
                "AND r.estado IN ('PENDIENTE', 'APROBADA') " +
                "AND r.fechaHoraInicio < :fin " +
                "AND r.fechaHoraFin > :inicio",
                Long.class
            )
            .setParameter("estacionamientoId", estacionamientoId)
            .setParameter("inicio", inicio)
            .setParameter("fin", fin)
            .getSingleResult();

            return cantidad > 0;

        } finally {
            em.close();
        }
    }

    public Usuario findUsuarioConRol(Long usuarioId) {
        EntityManager em = entityManagerSupplier.get();

        try {
            return em.createQuery(
                    "SELECT u FROM Usuario u "
                    + "JOIN FETCH u.rol "
                    + "WHERE u.id = :usuarioId",
                    Usuario.class
            )
            .setParameter("usuarioId", usuarioId)
            .getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
