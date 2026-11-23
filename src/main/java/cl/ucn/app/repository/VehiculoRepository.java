package cl.ucn.app.repository;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.hibernate.exception.ConstraintViolationException;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Vehiculo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class VehiculoRepository {

    private final Supplier<EntityManager> entityManagerSupplier;

    public VehiculoRepository() {
        this(JPAUtil::getEntityManager);
    }

    public VehiculoRepository(Supplier<EntityManager> entityManagerSupplier) {
        this.entityManagerSupplier = Objects.requireNonNull(entityManagerSupplier);
    }

    public void save(Vehiculo vehiculo) {
        try (EntityManager em = entityManagerSupplier.get()) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                em.persist(vehiculo);
                transaction.commit();
            } catch (RuntimeException e) {
                try {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                } catch (RuntimeException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                if (esPatenteDuplicada(e)) {
                    throw new IllegalArgumentException("La patente ya está registrada.", e);
                }
                throw e;
            }
        }
    }

    /** Busca la patente exacta; la normalización corresponde al servicio. */
    public Vehiculo findByPatente(String patente) {
        try (EntityManager em = entityManagerSupplier.get()) {
            try {
                return em.createQuery(
                                "SELECT v FROM Vehiculo v JOIN FETCH v.propietario WHERE v.patente = :patente",
                                Vehiculo.class)
                        .setParameter("patente", patente)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }
    }

    public List<Vehiculo> findByUsuarioId(Long usuarioId) {
        try (EntityManager em = entityManagerSupplier.get()) {
            return em.createQuery(
                            "SELECT v FROM Vehiculo v JOIN FETCH v.propietario "
                                    + "WHERE v.propietario.id = :usuarioId ORDER BY v.id",
                            Vehiculo.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
        }
    }

    public boolean existsByPatente(String patente) {
        try (EntityManager em = entityManagerSupplier.get()) {
            return em.createQuery(
                            "SELECT COUNT(v) FROM Vehiculo v WHERE v.patente = :patente",
                            Long.class)
                    .setParameter("patente", patente)
                    .getSingleResult() > 0;
        }
    }

    private boolean esPatenteDuplicada(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            // Nombre que PostgreSQL asigna al UNIQUE de patente en la migración 001.
            if (cause instanceof ConstraintViolationException violation
                    && "23505".equals(violation.getSQLState())
                    && "vehiculos_patente_key".equals(violation.getConstraintName())) {
                return true;
            }
        }
        return false;
    }

    public Vehiculo findById(Long id) {
        try (EntityManager em = entityManagerSupplier.get()) {
            try {
                return em.createQuery(
                        "SELECT v FROM Vehiculo v "
                        + "JOIN FETCH v.propietario "
                        + "WHERE v.id = :id",
                        Vehiculo.class)
                    .setParameter("id", id)
                    .getSingleResult();

            } catch (NoResultException e) {
                return null;
            }
        }
    }
}