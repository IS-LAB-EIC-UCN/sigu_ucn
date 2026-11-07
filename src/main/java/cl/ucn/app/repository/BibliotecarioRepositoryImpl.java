package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Bibliotecario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class BibliotecarioRepositoryImpl implements BibliotecarioRepository {

    @Override
    public Optional<Bibliotecario> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Bibliotecario.class, id));
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Bibliotecario> findByRut(String rut) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Bibliotecario> q = em.createQuery("SELECT b FROM Bibliotecario b WHERE b.rut = :rut", Bibliotecario.class);
            q.setParameter("rut", rut);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Bibliotecario> findByCorreo(String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Bibliotecario> q = em.createQuery("SELECT b FROM Bibliotecario b WHERE b.correo = :correo", Bibliotecario.class);
            q.setParameter("correo", correo);
            return Optional.of(q.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Bibliotecario> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT b FROM Bibliotecario b ORDER BY b.nombre", Bibliotecario.class).getResultList();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Bibliotecario save(Bibliotecario bibliotecario) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.persist(bibliotecario);
            if (!managed) em.getTransaction().commit();
            return bibliotecario;
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }
}
