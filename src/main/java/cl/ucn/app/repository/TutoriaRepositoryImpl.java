package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.dto.TutoriaFiltro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TutoriaRepositoryImpl implements TutoriaRepository {

    @Override
    public Optional<Tutoria> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Tutoria> query = em.createQuery(
                    "SELECT t FROM Tutoria t JOIN FETCH t.asignatura " +
                            "JOIN FETCH t.tutor tu JOIN FETCH tu.estudiante WHERE t.id = :id",
                    Tutoria.class
            );
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Tutoria> buscarDisponibles(TutoriaFiltro filtro, LocalDateTime ahora) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                StringBuilder jpql = new StringBuilder(
                        "SELECT t FROM Tutoria t " +
                                "JOIN FETCH t.asignatura a " +
                                "JOIN FETCH t.tutor tu " +
                                "JOIN FETCH tu.estudiante e " +
                                "WHERE t.estado = :estado AND t.inicio > :ahora");
                if (filtro.tutorId() != null) {
                    jpql.append(" AND tu.id = :tutorId");
                }
                if (filtro.asignaturaId() != null) {
                    jpql.append(" AND a.id = :asignaturaId");
                }
                if (filtro.fecha() != null) {
                    jpql.append(" AND t.inicio >= :desde AND t.inicio < :hasta");
                }
                jpql.append(" ORDER BY t.inicio");

                TypedQuery<Tutoria> query = em.createQuery(jpql.toString(), Tutoria.class);
                query.setParameter("estado", EstadoTutoria.PUBLICADA);
                query.setParameter("ahora", ahora);
                if (filtro.tutorId() != null) {
                    query.setParameter("tutorId", filtro.tutorId());
                }
                if (filtro.asignaturaId() != null) {
                    query.setParameter("asignaturaId", filtro.asignaturaId());
                }
                if (filtro.fecha() != null) {
                    LocalDateTime desde = filtro.fecha().atStartOfDay();
                    query.setParameter("desde", desde);
                    query.setParameter("hasta", desde.plusDays(1));
                }
                return query.getResultList();
            } finally {
                em.close();
            }
        }

    @Override
    public List<Tutoria> findByTutorId(Long tutorId) {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                TypedQuery<Tutoria> query = em.createQuery(
                        "SELECT t FROM Tutoria t JOIN FETCH t.asignatura " +
                                "JOIN FETCH t.tutor tu JOIN FETCH tu.estudiante " +
                                "WHERE tu.id = :tutorId ORDER BY t.inicio",
                        Tutoria.class);
                query.setParameter("tutorId", tutorId);
                return query.getResultList();
            } finally {
                em.close();
            }
    }

    @Override
    public long countByAsignatura(Long asignaturaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(t) FROM Tutoria t WHERE t.asignatura.id = :asignaturaId",
                    Long.class
            );
            query.setParameter("asignaturaId", asignaturaId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByAsignaturaAndEstado(Long asignaturaId, EstadoTutoria estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(t) FROM Tutoria t " +
                            "WHERE t.asignatura.id = :asignaturaId AND t.estado = :estado",
                    Long.class
            );
            query.setParameter("asignaturaId", asignaturaId);
            query.setParameter("estado", estado);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Tutoria tutoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(tutoria);
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
