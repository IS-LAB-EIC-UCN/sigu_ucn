package cl.ucn.app.repository.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class LibroRepository implements ILibroRepository {

    public void save(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            save(libro, em);
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

    public void save(Libro libro, EntityManager em) {
        if (libro.getId() == null) {
            em.persist(libro);
        } else {
            em.merge(libro);
        }
    }

    public void delete(Libro libro) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Libro managed = libro.getId() != null ? em.find(Libro.class, libro.getId()) : libro;
            if (managed != null) {
                em.remove(managed);
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

    public Libro findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Libro.class, id);
        } finally {
            em.close();
        }
    }

    public List<Libro> findByTitulo(String titulo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Libro> query = em.createQuery(
                    "SELECT l FROM Libro l WHERE LOWER(l.titulo) LIKE LOWER(:titulo)", Libro.class);
            query.setParameter("titulo", "%" + titulo + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Libro> findByAutor(String autor) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Libro> query = em.createQuery(
                    "SELECT l FROM Libro l WHERE LOWER(l.autor) LIKE LOWER(:autor)", Libro.class);
            query.setParameter("autor", "%" + autor + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Libro> findByCategoria(String categoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Libro> query = em.createQuery(
                    "SELECT l FROM Libro l WHERE LOWER(l.categoria) LIKE LOWER(:categoria)", Libro.class);
            query.setParameter("categoria", "%" + categoria + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Libro findByIsbn(String isbn) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Libro> query = em.createQuery(
                    "SELECT l FROM Libro l WHERE l.isbn = :isbn", Libro.class);
            query.setParameter("isbn", isbn);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Libro> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Libro> query = em.createQuery("SELECT l FROM Libro l", Libro.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}