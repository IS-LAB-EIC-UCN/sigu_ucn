package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.exceptions.BusinessException;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Libro;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

public class EjemplarService {

    private final EjemplarRepository ejemplarRepository;
    private final LibroRepository libroRepository;

    public EjemplarService() {
        this.ejemplarRepository = new EjemplarRepository();
        this.libroRepository = new LibroRepository();
    }

    public Ejemplar agregarEjemplar(Long libroId) {
        Libro libro = libroRepository.findById(libroId);

        if (libro == null) {
            throw new RecursoNoEncontradoException("No se puede agregar un ejemplar: El libro con ID " + libroId + " no existe.");
        }

        Ejemplar nuevoEjemplar = new Ejemplar();
        nuevoEjemplar.setLibro(libro);
        nuevoEjemplar.setEstado("DISPONIBLE");

        ejemplarRepository.save(nuevoEjemplar);

        return nuevoEjemplar;
    }

    public int agregarEjemplares(Long libroId, int cantidad) {
        Libro libro = libroRepository.findById(libroId);
        if (libro == null) {
            throw new RecursoNoEncontradoException("No se puede agregar un ejemplar: El libro con ID " + libroId + " no existe.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            for (int i = 0; i < cantidad; i++) {
                Ejemplar e = new Ejemplar();
                e.setLibro(libro);
                e.setEstado("DISPONIBLE");
                em.persist(e);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
        return cantidad;
    }

    public Ejemplar actualizarEjemplar(Long id, String estado) {
        Ejemplar ejemplar = ejemplarRepository.findById(id);
        if (ejemplar == null) {
            throw new RecursoNoEncontradoException("No existe un ejemplar con ID " + id);
        }
        if (!"DISPONIBLE".equalsIgnoreCase(estado) && !"PRESTADO".equalsIgnoreCase(estado)) {
            throw new ConflictoEstadoException("Estado invalido. Use DISPONIBLE o PRESTADO");
        }
        ejemplar.setEstado(estado);
        ejemplarRepository.save(ejemplar);
        return ejemplar;
    }

    public void eliminarEjemplar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            String estado = (String) em.createQuery(
                "SELECT e.estado FROM Ejemplar e WHERE e.id = :id")
                .setParameter("id", id)
                .getSingleResult();
            if ("PRESTADO".equalsIgnoreCase(estado)) {
                em.getTransaction().rollback();
                throw new ConflictoEstadoException("No se puede eliminar un ejemplar que esta prestado");
            }
            em.createNativeQuery("DELETE FROM multa WHERE prestamo_id IN (SELECT id FROM prestamo WHERE ejemplar_id = ?)")
                .setParameter(1, id)
                .executeUpdate();
            em.createNativeQuery("DELETE FROM prestamo WHERE ejemplar_id = ?")
                .setParameter(1, id)
                .executeUpdate();
            em.createNativeQuery("DELETE FROM ejemplar WHERE id = ?")
                .setParameter(1, id)
                .executeUpdate();
            em.getTransaction().commit();
        } catch (BusinessException ex) {
            throw ex;
        } catch (jakarta.persistence.NoResultException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RecursoNoEncontradoException("No existe un ejemplar con ID " + id);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Ejemplar> listarPorLibro(Long libroId) {
        if (libroId == null) {
            return new ArrayList<>();
        }
        Libro libro = libroRepository.findById(libroId);
        if (libro == null) {
            return new ArrayList<>();
        }
        return ejemplarRepository.findByLibro(libro);
    }
}