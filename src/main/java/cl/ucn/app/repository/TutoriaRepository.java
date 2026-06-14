package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Tutoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class TutoriaRepository {

    public void save(Tutoria tutoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(tutoria);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Tutoria tutoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(tutoria);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Tutoria findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Tutoria.class, id);
        } finally {
            em.close();
        }
    }

    public List<Tutoria> findAll() {
        return findFiltradas(null, null, null, null);
    }

    public List<Tutoria> findDisponibles() {
        return findFiltradas(null, null, null, "DISPONIBLE");
    }

    public List<Tutoria> findByTutor(Long tutorId) {
        return findFiltradas(tutorId, null, null, null);
    }

    public List<Tutoria> findFiltradas(Long tutorId, Long asignaturaId, LocalDate fecha, String estado) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT t FROM Tutoria t " +
                            "JOIN FETCH t.usuario " +
                            "JOIN FETCH t.asignatura " +
                            "WHERE 1 = 1 "
            );

            if (tutorId != null) {
                jpql.append("AND t.usuario.id = :tutorId ");
            }

            if (asignaturaId != null) {
                jpql.append("AND t.asignatura.id = :asignaturaId ");
            }

            if (fecha != null) {
                jpql.append("AND t.fecha = :fecha ");
            }

            if (estado != null && !estado.isBlank()) {
                jpql.append("AND t.estado = :estado ");
            }

            jpql.append("ORDER BY t.fecha, t.horaInicio");

            TypedQuery<Tutoria> query = em.createQuery(jpql.toString(), Tutoria.class);

            if (tutorId != null) {
                query.setParameter("tutorId", tutorId);
            }

            if (asignaturaId != null) {
                query.setParameter("asignaturaId", asignaturaId);
            }

            if (fecha != null) {
                query.setParameter("fecha", fecha);
            }

            if (estado != null && !estado.isBlank()) {
                query.setParameter("estado", estado);
            }

            return query.getResultList();

        } finally {
            em.close();
        }
    }
}