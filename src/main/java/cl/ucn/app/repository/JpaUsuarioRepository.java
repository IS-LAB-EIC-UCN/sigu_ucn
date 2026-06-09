package cl.ucn.app.repository;

import cl.ucn.app.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Optional;

public class JpaUsuarioRepository implements UsuarioRepository {
    @Override
    public void save(Usuario usuario) {
        EntityManager em = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(usuario);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Usuario> findById(String id) {
        EntityManager em = JpaConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(Usuario.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        EntityManager em = JpaConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }
}
