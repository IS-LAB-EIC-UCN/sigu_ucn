package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Ejemplar;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
public class PrestamoLibroService {
    private final LectorRepository lectorRepository;
    private final EjemplarRepository ejemplarRepository;
    private final PrestamoLibroRepository prestamoLibroRepository;
    private final LibroRepository libroRepository;

    public PrestamoLibroService(){
        this.lectorRepository = new LectorRepository();
        this.ejemplarRepository = new EjemplarRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
        this.libroRepository = new LibroRepository();
    }
    //para Testing
    PrestamoLibroService(PrestamoLibroRepository prestamoLibroRepository,
                         LectorRepository lectorRepository,
                         EjemplarRepository ejemplarRepository,
                         LibroRepository libroRepository) {
        this.prestamoLibroRepository = prestamoLibroRepository;
        this.lectorRepository = lectorRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.libroRepository = libroRepository;
    }

    public PrestamoLibro solicitarPrestamo(Long lectorId, Long libroId, LocalDate fechaVencimiento){

        if (fechaVencimiento == null || fechaVencimiento.isBefore(LocalDate.now())) {
            throw new ValidacionException("La fecha de vencimiento no puede ser nula ni anterior a hoy");
        }
        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null) {throw new RecursoNoEncontradoException("ID de lector" + lectorId +" inexistente");}

        Libro libro = libroRepository.findById(libroId);
        if (libro == null) {throw new RecursoNoEncontradoException("Libro no existe");}

        List<Ejemplar> disponibles = ejemplarRepository.findDisponiblesByLibro(libro);
        if (disponibles.isEmpty()){
            throw new ConflictoEstadoException("No hay ejemplares disponibles para este libro");
        }
        Ejemplar ejemplar = disponibles.get(0);

        if (lector.isBloqueado()){throw new ConflictoEstadoException("Lector bloqueado");}

        ejemplar.setEstado("PRESTADO");
        PrestamoLibro ejemplarPrestado = new PrestamoLibro();
        ejemplarPrestado.setFechaInicio(LocalDate.now());
        ejemplarPrestado.setFechaVencimiento(fechaVencimiento);
        ejemplarPrestado.setEstado("ACTIVO");
        ejemplarPrestado.setLector(lector);
        ejemplarPrestado.setEjemplar(ejemplar);

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ejemplarRepository.save(ejemplar, em);
            prestamoLibroRepository.save(ejemplarPrestado, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }

        return ejemplarPrestado;

    }
}
