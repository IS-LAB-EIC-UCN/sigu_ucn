package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Estudiante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class EstudianteRepositoryImpl implements EstudianteRepository {

    @Override
    public Optional<Estudiante> findByCorreo(String correo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Estudiante> query = em.createQuery(
                    "SELECT e FROM Estudiante e LEFT JOIN FETCH e.usuario " +
                            "WHERE LOWER(e.correo) = LOWER(:correo)",
                    Estudiante.class
            );
            query.setParameter("correo", correo);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Estudiante> findByUsuarioId(Long usuarioId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Estudiante> query = em.createQuery(
                    "SELECT e FROM Estudiante e JOIN FETCH e.usuario u WHERE u.id = :usuarioId",
                    Estudiante.class
            );
            query.setParameter("usuarioId", usuarioId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Estudiante> findByUsuarioId(EntityManager em, Long usuarioId) {
        return em.createQuery(
                        "SELECT e FROM Estudiante e WHERE e.usuario.id = :usuarioId",
                        Estudiante.class)
                .setParameter("usuarioId", usuarioId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean cursaAsignatura(EntityManager em, Long estudianteId, Long asignaturaId) {
        Long cantidad = em.createQuery(
                        "SELECT COUNT(a) FROM Estudiante e JOIN e.asignaturasCursadas a " +
                                "WHERE e.id = :estudianteId AND a.id = :asignaturaId",
                        Long.class)
                .setParameter("estudianteId", estudianteId)
                .setParameter("asignaturaId", asignaturaId)
                .getSingleResult();
        return cantidad > 0;
    }
}
