
package cl.ucn.app.repository;

import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;

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
        try (EntityManager em = entityManagerSupplier.get()) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.persist(reserva);
                transaction.commit();
            } catch (RuntimeException e) {
                try {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                } catch (RuntimeException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw traducirConflicto(e);
            }
        }
    }

    private RuntimeException traducirConflicto(RuntimeException error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            String constraint = null;
            if (cause instanceof ConstraintViolationException violation
                    && "23P01".equals(violation.getSQLState())) {
                constraint = violation.getConstraintName();
            }
            // Hibernate 6 puede omitir el nombre para exclusiones. El driver conserva el dato estructurado.
            if (cause instanceof PSQLException postgres && "23P01".equals(postgres.getSQLState())
                    && postgres.getServerErrorMessage() != null) {
                constraint = postgres.getServerErrorMessage().getConstraint();
            }
            if ("excl_reservas_est_espacio_horario".equals(constraint)) {
                return new IllegalArgumentException("El estacionamiento ya está reservado en ese horario.", error);
            }
            if ("excl_reservas_est_vehiculo_horario".equals(constraint)) {
                return new IllegalArgumentException("El vehículo ya tiene una reserva en ese horario.", error);
            }
            if (cause instanceof SQLException sql
                    && ("40P01".equals(sql.getSQLState()) || "40001".equals(sql.getSQLState()))) {
                return new IllegalArgumentException(
                        "Otra solicitud coincidió con esta reserva. Revisa la disponibilidad e inténtalo nuevamente.", error);
            }
        }
        return error;
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

    public boolean existsOverlappingReservationForVehicle(
            Long vehiculoId, LocalDateTime inicio, LocalDateTime fin) {
        try (EntityManager em = entityManagerSupplier.get()) {
            return em.createQuery(
                    "SELECT COUNT(r) FROM ReservaEstacionamiento r "
                            + "WHERE r.vehiculo.id = :vehiculoId "
                            + "AND r.estado IN ('PENDIENTE', 'APROBADA') "
                            + "AND r.fechaHoraInicio < :fin "
                            + "AND r.fechaHoraFin > :inicio", Long.class)
                    .setParameter("vehiculoId", vehiculoId)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getSingleResult() > 0;
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
