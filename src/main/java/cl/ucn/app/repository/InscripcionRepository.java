package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Inscripcion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class InscripcionRepository {

    public void save(Inscripcion inscripcion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (inscripcion.getId() == null) {
                em.persist(inscripcion);
            } else {
                em.merge(inscripcion);
            }
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

    // Cuenta personas inscritas en un taller
    public Long countByTallerAndEstado(Long tallerId, String estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(i) FROM Inscripcion i WHERE i.taller.id = :tallerId AND i.estado = :estado", Long.class);
            query.setParameter("tallerId", tallerId);
            query.setParameter("estado", estado);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    // Verifica si el estudiante ya se intento inscribir antes
    public Inscripcion findByTallerAndUsuario(Long tallerId, Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Inscripcion> query = em.createQuery(
                    "SELECT i FROM Inscripcion i WHERE i.taller.id = :tallerId AND i.usuario.id = :usuarioId", Inscripcion.class);
            query.setParameter("tallerId", tallerId);
            query.setParameter("usuarioId", usuarioId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Retorna null si nunca se ha inscrito
        } finally {
            em.close();
        }
    }

    // Busca y devuelve a la persona que lleva mas tiempo en lista de espera
    public Inscripcion findFirstEnEspera(Long tallerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Inscripcion> query = em.createQuery(
                    "SELECT i FROM Inscripcion i WHERE i.taller.id = :tallerId AND i.estado = 'EN_ESPERA' ORDER BY i.fechaInscripcion ASC", Inscripcion.class);
            query.setParameter("tallerId", tallerId);
            query.setMaxResults(1); // Solo trae al primero de la fila
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    
    // Devuelve una lista de todos los talleres a los que este inscrito un usuario
    public List<Inscripcion> findByUsuario(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Inscripcion> query = em.createQuery(
                    "SELECT i FROM Inscripcion i JOIN FETCH i.taller t WHERE i.usuario.id = :usuarioId", Inscripcion.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Devuelve una lista de todas las inscripciones (alumnos) de un taller específico
    public List<Inscripcion> findByTallerId(Long tallerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Inscripcion> query = em.createQuery(
                    "SELECT i FROM Inscripcion i JOIN FETCH i.usuario u WHERE i.taller.id = :tallerId", Inscripcion.class);
            query.setParameter("tallerId", tallerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
