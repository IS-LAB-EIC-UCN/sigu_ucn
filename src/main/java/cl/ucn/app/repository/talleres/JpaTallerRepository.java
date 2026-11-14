package cl.ucn.app.repository.talleres;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.dto.talleres.TallerResumen;
import cl.ucn.app.model.talleres.EstadoInscripcion;
import cl.ucn.app.model.talleres.EstadoTaller;
import cl.ucn.app.model.talleres.Taller;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Implementación de TallerRepository con JPA/Hibernate sobre PostgreSQL. */
public class JpaTallerRepository implements TallerRepository {

    /** Proyección común: taller + conteo de inscritos y en espera (subconsultas). */
    private static final String SELECT_RESUMEN = """
            SELECT new cl.ucn.app.dto.talleres.TallerResumen(
                t.id, t.nombre, t.categoria, t.fecha, t.horaInicio, t.horaFin, t.lugar, t.cupoMaximo,
                (SELECT COUNT(i) FROM Inscripcion i WHERE i.taller = t AND i.estado = :inscrito),
                (SELECT COUNT(e) FROM Inscripcion e WHERE e.taller = t AND e.estado = :enEspera))
            FROM Taller t
            """;

    @Override
    public Taller save(Taller taller) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Taller guardado;
            if (taller.getId() == null) {
                em.persist(taller);
                guardado = taller;
            } else {
                guardado = em.merge(taller);
            }
            em.getTransaction().commit();
            return guardado;
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
    public Optional<Taller> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Taller.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<TallerResumen> buscarDisponibles(LocalDate desde, String categoria, LocalDate fecha) {
        StringBuilder jpql = new StringBuilder(SELECT_RESUMEN)
                .append(" WHERE t.estado = :activo AND t.fecha >= :desde");
        boolean filtrarCategoria = categoria != null && !categoria.isBlank();
        if (filtrarCategoria) {
            jpql.append(" AND t.categoria = :categoria");
        }
        if (fecha != null) {
            jpql.append(" AND t.fecha = :fecha");
        }
        jpql.append(" ORDER BY t.fecha, t.horaInicio");

        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<TallerResumen> query = em.createQuery(jpql.toString(), TallerResumen.class);
            setEstados(query);
            query.setParameter("activo", EstadoTaller.ACTIVO);
            query.setParameter("desde", desde);
            if (filtrarCategoria) {
                query.setParameter("categoria", categoria);
            }
            if (fecha != null) {
                query.setParameter("fecha", fecha);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<TallerResumen> findResumenById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<TallerResumen> query = em.createQuery(
                    SELECT_RESUMEN + " WHERE t.id = :id", TallerResumen.class);
            setEstados(query);
            query.setParameter("id", id);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public List<String> listarCategorias() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT t.categoria FROM Taller t ORDER BY t.categoria", String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    private void setEstados(TypedQuery<?> query) {
        query.setParameter("inscrito", EstadoInscripcion.INSCRITO);
        query.setParameter("enEspera", EstadoInscripcion.EN_ESPERA);
    }
}
