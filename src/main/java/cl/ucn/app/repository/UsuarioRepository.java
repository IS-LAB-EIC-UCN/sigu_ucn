package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class UsuarioRepository {

    public Usuario findByCorreo(String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u JOIN FETCH u.rol WHERE lower(u.correo) = :correo",
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

    public void save(Usuario usuario) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            usuario.setPassword(cl.ucn.app.service.PasswordHasher.generar(usuario.getPassword()));
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

    /** Migración idempotente para bases anteriores. Se ejecuta antes de atender peticiones. */
    public int migrarPasswords() {
        try (EntityManager em=JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                int migrados=0;
                for(Usuario usuario:em.createQuery("FROM Usuario",Usuario.class).setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE).getResultList()) {
                    String valor=usuario.getPassword();
                    if(cl.ucn.app.service.PasswordHasher.esHash(valor)) continue;
                    if(cl.ucn.app.service.PasswordHasher.tienePrefijo(valor)) throw new IllegalStateException("Hay un hash de contraseña con formato inválido.");
                    usuario.setPassword(cl.ucn.app.service.PasswordHasher.generar(valor));migrados++;
                }
                em.getTransaction().commit();return migrados;
            } catch(RuntimeException ex) { if(em.getTransaction().isActive()) em.getTransaction().rollback();throw ex; }
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
}