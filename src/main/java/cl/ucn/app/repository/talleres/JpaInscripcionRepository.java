package cl.ucn.app.repository.talleres;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.talleres.EstadoInscripcion;
import cl.ucn.app.model.talleres.Inscripcion;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/** Implementación de InscripcionRepository con JPA/Hibernate sobre PostgreSQL. */
public class JpaInscripcionRepository implements InscripcionRepository {

    /** Trae taller y usuario en la misma consulta para poder usarlos en las vistas (evita LazyInitializationException). */
    private static final String SELECT_CON_RELACIONES =
            "SELECT i FROM Inscripcion i JOIN FETCH i.taller JOIN FETCH i.usuario ";

    @Override
    public Inscripcion save(Inscripcion inscripcion) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Inscripcion guardada;
            if (inscripcion.getId() == null) {
                em.persist(inscripcion);
                guardada = inscripcion;
            } else {
                guardada = em.merge(inscripcion);
            }
            em.getTransaction().commit();
            return guardada;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void updateAll(List<Inscripcion> inscripciones) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            for (Inscripcion inscripcion : inscripciones) {
                em.merge(inscripcion);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Inscripcion> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(SELECT_CON_RELACIONES + "WHERE i.id = :id", Inscripcion.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByTallerAndEstado(Long tallerId, EstadoInscripcion estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(i) FROM Inscripcion i WHERE i.taller.id = :tallerId AND i.estado = :estado",
                            Long.class)
                    .setParameter("tallerId", tallerId)
                    .setParameter("estado", estado)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Inscripcion> findVigentesByUsuario(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(SELECT_CON_RELACIONES
                                    + "WHERE i.usuario.id = :usuarioId AND i.estado <> :anulada",
                            Inscripcion.class)
                    .setParameter("usuarioId", usuarioId)
                    .setParameter("anulada", EstadoInscripcion.ANULADA)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Inscripcion> findByUsuario(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(SELECT_CON_RELACIONES
                                    + "WHERE i.usuario.id = :usuarioId ORDER BY i.taller.fecha DESC, i.fechaInscripcion DESC",
                            Inscripcion.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Inscripcion> findByTallerAndEstado(Long tallerId, EstadoInscripcion estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(SELECT_CON_RELACIONES
                                    + "WHERE i.taller.id = :tallerId AND i.estado = :estado ORDER BY i.fechaInscripcion, i.id",
                            Inscripcion.class)
                    .setParameter("tallerId", tallerId)
                    .setParameter("estado", estado)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Inscripcion> findPrimeraEnEspera(Long tallerId) {
        return findByTallerAndEstado(tallerId, EstadoInscripcion.EN_ESPERA).stream().findFirst();
    }
}
