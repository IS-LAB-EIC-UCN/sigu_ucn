package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Categoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class CategoriaRepositoryImpl implements CategoriaRepository {

    @Override
    public Optional<Categoria> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Categoria.class, id));
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Categoria> findByNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Categoria> q = em.createQuery("SELECT c FROM Categoria c WHERE c.nombre = :nombre", Categoria.class);
            q.setParameter("nombre", nombre);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Categoria> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Categoria> q = em.createQuery("SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class);
            return q.getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Categoria save(Categoria categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.persist(categoria);
            if (!managed) em.getTransaction().commit();
            return categoria;
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }
}
