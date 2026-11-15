package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class RegistroRepositoryImpl implements RegistroRepository {

    @Override
    public boolean existeUsuarioConCorreo(String correo) {
        return contar("SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.correo) = LOWER(:correo)", correo) > 0;
    }

    @Override
    public boolean existeEstudianteConCorreo(String correo) {
        return contar("SELECT COUNT(e) FROM Estudiante e WHERE LOWER(e.correo) = LOWER(:correo)", correo) > 0;
    }

    @Override
    public Rol buscarRol(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Rol> query = em.createQuery(
                    "SELECT r FROM Rol r WHERE r.nombre = :nombre", Rol.class);
            query.setParameter("nombre", nombre);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void guardar(Usuario usuario, Estudiante estudiante) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (usuario.getRol() != null && usuario.getRol().getId() != null) {
                usuario.setRol(em.getReference(Rol.class, usuario.getRol().getId()));
            }
            em.persist(usuario);
            estudiante.setUsuario(usuario);
            em.persist(estudiante);
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

    private long contar(String jpql, String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("correo", correo);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
}
