package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.config.JpaTransaccion;
import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.model.EstadoInscripcion;
import cl.ucn.app.model.EstadoTutoria;
import cl.ucn.app.model.Tutoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Tutoria> findByIdForUpdate(EntityManager em, Long id) {
        // Bloquea solo la fila de la tutoría; asignatura y tutor se cargan perezosamente con el mismo em.
        return Optional.ofNullable(em.find(Tutoria.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    @Override
    public void save(EntityManager em, Tutoria tutoria) {
        em.persist(tutoria);
        em.flush();
    }

    @Override
    public boolean existeConflictoTutor(EntityManager em, Long tutorId, LocalDateTime inicio, LocalDateTime fin) {
        Long cantidad = em.createQuery(
                        "SELECT COUNT(t) FROM Tutoria t WHERE t.tutor.id = :tutorId AND t.estado = :estado " +
                                "AND t.inicio < :fin AND t.fin > :inicio",
                        Long.class)
                .setParameter("tutorId", tutorId)
                .setParameter("estado", EstadoTutoria.PUBLICADA)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
        return cantidad > 0;
    }

    @Override
    public boolean existeConflictoEstudiante(EntityManager em, Long estudianteId,
                                             LocalDateTime inicio, LocalDateTime fin) {
        Long cantidad = em.createQuery(
                        "SELECT COUNT(i) FROM InscripcionTutoria i WHERE i.estudiante.id = :estudianteId " +
                                "AND i.estado = :reservada AND i.tutoria.estado = :publicada " +
                                "AND i.tutoria.inicio < :fin AND i.tutoria.fin > :inicio",
                        Long.class)
                .setParameter("estudianteId", estudianteId)
                .setParameter("reservada", EstadoInscripcion.RESERVADA)
                .setParameter("publicada", EstadoTutoria.PUBLICADA)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
        return cantidad > 0;
    }

    @Override
    public long contarInscripcionesActivas(EntityManager em, Long tutoriaId) {
        return em.createQuery(
                        "SELECT COUNT(i) FROM InscripcionTutoria i " +
                                "WHERE i.tutoria.id = :tutoriaId AND i.estado = :estado",
                        Long.class)
                .setParameter("tutoriaId", tutoriaId)
                .setParameter("estado", EstadoInscripcion.RESERVADA)
                .getSingleResult();
    }

    @Override
    public List<Tutoria> buscarDisponibles(TutoriaFiltro filtro) {
        TutoriaFiltro f = filtro == null ? TutoriaFiltro.vacio() : filtro;

        StringBuilder jpql = new StringBuilder(
                "SELECT t FROM Tutoria t JOIN FETCH t.asignatura a JOIN FETCH t.tutor tu " +
                        "JOIN FETCH tu.estudiante WHERE t.estado = :estado AND t.inicio > :ahora");
        if (f.asignaturaId() != null) {
            jpql.append(" AND a.id = :asignaturaId");
        }
        if (f.tutorId() != null) {
            jpql.append(" AND tu.id = :tutorId");
        }
        if (f.fecha() != null) {
            jpql.append(" AND t.inicio >= :desde AND t.inicio < :hasta");
        }
        jpql.append(" ORDER BY t.inicio");

        return JpaTransaccion.leer(em -> {
            TypedQuery<Tutoria> query = em.createQuery(jpql.toString(), Tutoria.class);
            query.setParameter("estado", EstadoTutoria.PUBLICADA);
            query.setParameter("ahora", LocalDateTime.now());
            if (f.asignaturaId() != null) {
                query.setParameter("asignaturaId", f.asignaturaId());
            }
            if (f.tutorId() != null) {
                query.setParameter("tutorId", f.tutorId());
            }
            if (f.fecha() != null) {
                query.setParameter("desde", f.fecha().atStartOfDay());
                query.setParameter("hasta", f.fecha().plusDays(1).atStartOfDay());
            }
            return query.getResultList();
        });
    }

    @Override
    public List<Tutoria> findByTutorId(Long tutorId) {
        return JpaTransaccion.leer(em -> em.createQuery(
                        "SELECT t FROM Tutoria t JOIN FETCH t.asignatura JOIN FETCH t.tutor tu " +
                                "JOIN FETCH tu.estudiante WHERE tu.id = :tutorId ORDER BY t.inicio",
                        Tutoria.class)
                .setParameter("tutorId", tutorId)
                .getResultList());
    }

    @Override
    public List<Tutoria> findByEstudianteId(Long estudianteId) {
        return JpaTransaccion.leer(em -> em.createQuery(
                        "SELECT t FROM Tutoria t JOIN FETCH t.asignatura JOIN FETCH t.tutor tu " +
                                "JOIN FETCH tu.estudiante WHERE EXISTS (" +
                                "SELECT i.id FROM InscripcionTutoria i " +
                                "WHERE i.tutoria = t AND i.estudiante.id = :estudianteId) " +
                                "ORDER BY t.inicio",
                        Tutoria.class)
                .setParameter("estudianteId", estudianteId)
                .getResultList());
    }
}
