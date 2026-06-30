package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import cl.ucn.app.model.Rol;

import java.util.List;

public class UsuarioRepository {

    public Usuario findByCorreo(String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.correo = :correo",
                    Usuario.class
            );
            query.setParameter("correo", correo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Rol findRolByNombre(String nombreRol) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Rol> query = em.createQuery(
                    "SELECT r FROM Rol r WHERE r.nombre = :nombreRol",
                    Rol.class
            );

            query.setParameter("nombreRol", nombreRol);

            return query.getSingleResult();

        } catch (NoResultException e) {
            return null;

        } finally {
            em.close();
        }
    }

    public void save(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Usuario findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public List<Usuario> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    public List<Usuario> findByRol(String nombreRol) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.rol.nombre = :nombreRol ORDER BY u.nombre",
                    Usuario.class
            );

            query.setParameter("nombreRol", nombreRol);

            return query.getResultList();

        } finally {
            em.close();
        }
    }
}