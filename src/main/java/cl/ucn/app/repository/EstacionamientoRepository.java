
package cl.ucn.app.repository;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.model.TipoEstacionamiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
        EntityManager em = entityManagerSupplier.get();

        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            em.persist(estacionamiento);

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
