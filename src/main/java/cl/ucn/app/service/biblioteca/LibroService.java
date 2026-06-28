package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.exceptions.BusinessException;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import cl.ucn.app.service.biblioteca.api.ILibroService;
import cl.ucn.app.model.biblioteca.Libro;
import jakarta.persistence.EntityManager;
import java.util.List;

public class LibroService implements ILibroService {

    private final ILibroRepository libroRepository;

    public LibroService() {
        this.libroRepository = new LibroRepository();
    }

    public LibroService(ILibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public Libro registrarLibro(String titulo, String autor, String categoria, String isbn) {

        if (isbn == null || isbn.isBlank()) {
            throw new ValidacionException("El ISBN es obligatorio");
        }

        String isbnLimpio = isbn.replaceAll("[\\s-]", "");
        if (isbnLimpio.length() != 10 && isbnLimpio.length() != 13) {
            throw new ValidacionException("El ISBN debe tener 10 o 13 digitos (sin contar guiones)");
        }
        if (isbnLimpio.length() == 10 && !isbnLimpio.matches("^\\d{9}[\\dX]$")) {
            throw new ValidacionException("El ISBN-10 no tiene un formato valido");
        }
        if (isbnLimpio.length() == 13 && !isbnLimpio.matches("^\\d{13}$")) {
            throw new ValidacionException("El ISBN-13 no tiene un formato valido");
        }

        if (titulo.length() > 200 || autor.length() > 100 || categoria.length() > 100 || isbn.length() > 20) {
            throw new ValidacionException("Algun campo supera el largo maximo permitido");
        }

        if (libroRepository.findByIsbn(isbn) != null) {
            throw new ValidacionException("Ya existe un libro registrado con el ISBN: " + isbn);
        }

        Libro nuevoLibro = new Libro();
        nuevoLibro.setTitulo(titulo);
        nuevoLibro.setAutor(autor);
        nuevoLibro.setCategoria(categoria);
        nuevoLibro.setIsbn(isbn);

        libroRepository.save(nuevoLibro);
        return nuevoLibro;
    }

    public Libro actualizarLibro(Long id, String titulo, String autor, String categoria, String isbn) {
        if (id == null) {
            throw new RecursoNoEncontradoException("ID de libro no proporcionado");
        }
        Libro libro = libroRepository.findById(id);
        if (libro == null) {
            throw new RecursoNoEncontradoException("No existe un libro con ID " + id);
        }

        if (isbn == null || isbn.isBlank()) {
            throw new ValidacionException("El ISBN es obligatorio");
        }
        String isbnLimpio = isbn.replaceAll("[\\s-]", "");
        if (isbnLimpio.length() != 10 && isbnLimpio.length() != 13) {
            throw new ValidacionException("El ISBN debe tener 10 o 13 digitos (sin contar guiones)");
        }

        Libro existenteIsbn = libroRepository.findByIsbn(isbn);
        if (existenteIsbn != null && !existenteIsbn.getId().equals(id)) {
            throw new ValidacionException("Ya existe otro libro registrado con el ISBN: " + isbn);
        }

        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setCategoria(categoria);
        libro.setIsbn(isbn);
        libroRepository.save(libro);
        return libro;
    }

    public void eliminarLibro(Long id) {
        if (id == null) {
            throw new RecursoNoEncontradoException("ID de libro no proporcionado");
        }
        Libro libro = libroRepository.findById(id);
        if (libro == null) {
            throw new RecursoNoEncontradoException("No existe un libro con ID " + id);
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            List<cl.ucn.app.model.biblioteca.EstadoEjemplar> estados = em.createQuery(
                "SELECT e.estado FROM Ejemplar e WHERE e.libro.id = :id", cl.ucn.app.model.biblioteca.EstadoEjemplar.class)
                .setParameter("id", id)
                .getResultList();
            for (cl.ucn.app.model.biblioteca.EstadoEjemplar estado : estados) {
                if (cl.ucn.app.model.biblioteca.EstadoEjemplar.PRESTADO == estado) {
                    em.getTransaction().rollback();
                    throw new ConflictoEstadoException("No se puede eliminar el libro porque tiene ejemplares prestados. Primero devuelve los prestamos.");
                }
            }
            em.createNativeQuery("DELETE FROM multa WHERE prestamo_id IN (SELECT id FROM prestamo WHERE ejemplar_id IN (SELECT id FROM ejemplar WHERE libro_id = ?))")
                .setParameter(1, id)
                .executeUpdate();
            em.createNativeQuery("DELETE FROM prestamo WHERE ejemplar_id IN (SELECT id FROM ejemplar WHERE libro_id = ?)")
                .setParameter(1, id)
                .executeUpdate();
            em.createNativeQuery("DELETE FROM ejemplar WHERE libro_id = ?")
                .setParameter(1, id)
                .executeUpdate();
            em.createQuery("DELETE FROM Libro l WHERE l.id = :id")
                .setParameter("id", id)
                .executeUpdate();
            em.getTransaction().commit();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Libro> buscarPorTitulo(String titulo){
        return libroRepository.findByTitulo(titulo);
    }

    public List<Libro> buscarPorAutor(String autor){
        return libroRepository.findByAutor(autor);
    }

    public List<Libro> listarTodos(){
        return libroRepository.findAll();
    }
}