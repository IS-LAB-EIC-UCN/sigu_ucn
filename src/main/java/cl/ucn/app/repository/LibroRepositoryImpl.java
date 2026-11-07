package cl.ucn.app.repository;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LibroRepositoryImpl implements LibroRepository {

    @Override
    public Optional<Libro> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Libro> q = em.createQuery(
                "SELECT DISTINCT l FROM Libro l " +
                "LEFT JOIN FETCH l.categoria " +
                "LEFT JOIN FETCH l.autores " +
                "WHERE l.id = :id", Libro.class);
            q.setParameter("id", id);
            Libro libro = q.getSingleResult();
            cargarEjemplares(em, Collections.singletonList(libro));
            return Optional.of(libro);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public Optional<Libro> findByIsbn(String isbn) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Libro> q = em.createQuery(
                "SELECT DISTINCT l FROM Libro l " +
                "LEFT JOIN FETCH l.categoria " +
                "LEFT JOIN FETCH l.autores " +
                "WHERE l.isbn = :isbn", Libro.class);
            q.setParameter("isbn", isbn);
            Libro libro = q.getSingleResult();
            cargarEjemplares(em, Collections.singletonList(libro));
            return Optional.of(libro);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Libro> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Libro> libros = em.createQuery(
                "SELECT DISTINCT l FROM Libro l " +
                "LEFT JOIN FETCH l.categoria " +
                "LEFT JOIN FETCH l.autores " +
                "ORDER BY l.titulo", Libro.class).getResultList();
            cargarEjemplares(em, libros);
            return libros;
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    @Override
    public List<Libro> buscarPorCriterio(String termino) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String patron = "%" + (termino == null ? "" : termino.trim().toLowerCase()) + "%";
            List<Libro> libros = em.createQuery(
                "SELECT DISTINCT l FROM Libro l " +
                "LEFT JOIN FETCH l.categoria c " +
                "LEFT JOIN FETCH l.autores a " +
                "WHERE LOWER(l.titulo) LIKE :patron " +
                "   OR LOWER(c.nombre) LIKE :patron " +
                "   OR LOWER(a.nombre) LIKE :patron " +
                "ORDER BY l.titulo", Libro.class)
                .setParameter("patron", patron)
                .getResultList();
            cargarEjemplares(em, libros);
            return libros;
        } finally {
            if (!JPAUtil.isManagedTransaction()) em.close();
        }
    }

    private void cargarEjemplares(EntityManager em, List<Libro> libros) {
        if (libros == null || libros.isEmpty()) {
            return;
        }
        em.createQuery(
            "SELECT DISTINCT l FROM Libro l " +
            "LEFT JOIN FETCH l.ejemplares " +
            "WHERE l IN :libros", Libro.class)
            .setParameter("libros", libros)
            .getResultList();
    }

    @Override
    public Libro save(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.persist(libro);
            if (!managed) em.getTransaction().commit();
            return libro;
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }

    @Override
    public void update(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        boolean managed = JPAUtil.isManagedTransaction();
        try {
            if (!managed) em.getTransaction().begin();
            em.merge(libro);
            if (!managed) em.getTransaction().commit();
        } catch (Exception e) {
            if (!managed && em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (!managed) em.close();
        }
    }
}
