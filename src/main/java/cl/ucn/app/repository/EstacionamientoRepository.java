
package cl.ucn.app.repository;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.postgresql.util.PSQLException;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.TipoEstacionamiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;

public class EstacionamientoRepository {

    private final Supplier<EntityManager> entityManagerSupplier;

    public EstacionamientoRepository() {
        this(JPAUtil::getEntityManager);
    }

    public EstacionamientoRepository(Supplier<EntityManager> entityManagerSupplier) {
        this.entityManagerSupplier = Objects.requireNonNull(entityManagerSupplier);
    }

    public void save(Estacionamiento estacionamiento) {
        try {
            enTransaccion(em -> {
                em.persist(estacionamiento);
                return null;
            });
        } catch (RuntimeException error) {
            for (Throwable causa = error; causa != null; causa = causa.getCause()) {
                if (causa instanceof PSQLException postgres
                        && "23505".equals(postgres.getSQLState())
                        && postgres.getServerErrorMessage() != null
                        && "estacionamientos_codigo_key".equals(postgres.getServerErrorMessage().getConstraint())) {
                    throw new IllegalArgumentException("Ya existe un estacionamiento con ese código.", error);
                }
            }
            throw error;
        }
    }

    public <T> T conEstacionamientoBloqueado(
            Long id, BiFunction<EntityManager, Estacionamiento, T> operacion) {
        return enTransaccion(em -> {
            // Después de esperar el bloqueo, las consultas deben ver las reservas recién confirmadas.
            em.createNativeQuery("SET TRANSACTION ISOLATION LEVEL READ COMMITTED").executeUpdate();
            Estacionamiento estacionamiento = em.find(Estacionamiento.class, id, LockModeType.PESSIMISTIC_WRITE);
            return operacion.apply(em, estacionamiento);
        });
    }

    private <T> T enTransaccion(Function<EntityManager, T> operacion) {
        try (EntityManager em = entityManagerSupplier.get()) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                T resultado = operacion.apply(em);
                transaction.commit();
                return resultado;
            } catch (RuntimeException error) {
                try {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                } catch (RuntimeException rollbackError) {
                    error.addSuppressed(rollbackError);
                }
                throw error;
            }
        }
    }

    public Estacionamiento findById(Long id) {
        EntityManager em = entityManagerSupplier.get();

        try {
            return em.find(Estacionamiento.class, id);
        } finally {
            em.close();
        }
    }

    public List<Estacionamiento> findDisponiblesParaUsuario(boolean esDocente) {
        EntityManager em = entityManagerSupplier.get();

        try {
            String jpql;

            if (esDocente) {
                jpql = "SELECT e FROM Estacionamiento e "
                        + "WHERE e.habilitado = true "
                        + "ORDER BY e.codigo";
            } else {
                jpql = "SELECT e FROM Estacionamiento e "
                        + "WHERE e.habilitado = true "
                        + "AND e.tipo = :tipo "
                        + "ORDER BY e.codigo";
            }

            var query = em.createQuery(jpql, Estacionamiento.class);

            if (!esDocente) {
                query.setParameter("tipo", TipoEstacionamiento.COMUN);
            }

            return query.getResultList();

        } finally {
            em.close();
        }
    }

    public Estacionamiento findByCodigo(String codigo) {
        EntityManager em = entityManagerSupplier.get();

        try {
            return em.createQuery(
                    "SELECT e FROM Estacionamiento e WHERE e.codigo = :codigo",
                    Estacionamiento.class
            ).setParameter("codigo", codigo)
             .getSingleResult();

        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Estacionamiento> findAll() {
        EntityManager em = entityManagerSupplier.get();

        try {
            return em.createQuery(
                    "SELECT e FROM Estacionamiento e ORDER BY e.codigo",
                    Estacionamiento.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByCodigo(String codigo) {
        EntityManager em = entityManagerSupplier.get();

        try {
            return em.createQuery(
                    "SELECT COUNT(e) FROM Estacionamiento e WHERE e.codigo = :codigo",
                    Long.class
            ).setParameter("codigo", codigo)
             .getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
    
}
