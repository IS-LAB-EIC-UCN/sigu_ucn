package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Comentario;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ComentarioRepository {

    public void guardar(Comentario comentario) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(comentario);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Comentario buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Comentario.class, id);
        } finally {
            em.close();
        }
    }

    public List<Comentario> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Comentario c",
                    Comentario.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void actualizar(Comentario comentario) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(comentario);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            Comentario comentario = em.find(Comentario.class, id);

            if (comentario != null) {
                em.getTransaction().begin();
                em.remove(comentario);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }
}