package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Inscripcion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class InscripcionRepository {

    public Inscripcion save(Inscripcion inscripcion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (inscripcion.getId() == null) {
                em.persist(inscripcion);
            } else {
                inscripcion = em.merge(inscripcion);
            }
            em.getTransaction().commit();
            return inscripcion;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Inscripcion findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Inscripcion.class, id);
        } finally {
            em.close();
        }
    }

    public Inscripcion findByUsuarioAndEvento(Long usuarioId, Long eventoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Inscripcion> query = em.createQuery(
                    "SELECT i FROM Inscripcion i WHERE i.usuario.id = :usuarioId AND i.evento.id = :eventoId",
                    Inscripcion.class);
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("eventoId", eventoId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Inscripcion> findByEventoId(Long eventoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Inscripcion> query = em.createQuery(
                    "SELECT i FROM Inscripcion i JOIN FETCH i.usuario u JOIN FETCH u.rol WHERE i.evento.id = :eventoId ORDER BY i.fechaInscripcion",
                    Inscripcion.class);
            query.setParameter("eventoId", eventoId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Inscripcion inscripcion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Inscripcion managed = em.merge(inscripcion);
            em.remove(managed);
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
}
