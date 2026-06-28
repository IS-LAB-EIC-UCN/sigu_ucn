package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.IPrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import cl.ucn.app.repository.biblioteca.api.ILectorRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.service.biblioteca.api.IPrestamoService;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Ejemplar;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
public class PrestamoLibroService implements IPrestamoService {
    private final ILectorRepository lectorRepository;
    private final IEjemplarRepository ejemplarRepository;
    private final IPrestamoLibroRepository prestamoLibroRepository;
    private final ILibroRepository libroRepository;

    public PrestamoLibroService(){
        this.lectorRepository = new LectorRepository();
        this.ejemplarRepository = new EjemplarRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
        this.libroRepository = new LibroRepository();
    }
    //para Testing
    public PrestamoLibroService(IPrestamoLibroRepository prestamoLibroRepository,
                                ILectorRepository lectorRepository,
                                IEjemplarRepository ejemplarRepository,
                                ILibroRepository libroRepository) {
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

        ejemplar.setEstado(cl.ucn.app.model.biblioteca.EstadoEjemplar.PRESTADO);
        PrestamoLibro ejemplarPrestado = new PrestamoLibro();
        ejemplarPrestado.setFechaInicio(LocalDate.now());
        ejemplarPrestado.setFechaVencimiento(fechaVencimiento);
        ejemplarPrestado.setEstado(cl.ucn.app.model.biblioteca.EstadoPrestamo.SOLICITADO);
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

    public PrestamoLibro solicitarPrestamoActivo(Long lectorId, Long libroId, LocalDate fechaVencimiento) {
        if (fechaVencimiento == null || fechaVencimiento.isBefore(LocalDate.now())) {
            throw new ValidacionException("La fecha de vencimiento no puede ser nula ni anterior a hoy");
        }
        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null) {throw new RecursoNoEncontradoException("ID de lector " + lectorId +" inexistente");}

        Libro libro = libroRepository.findById(libroId);
        if (libro == null) {throw new RecursoNoEncontradoException("Libro no existe");}

        List<Ejemplar> disponibles = ejemplarRepository.findDisponiblesByLibro(libro);
        if (disponibles.isEmpty()){
            throw new ConflictoEstadoException("No hay ejemplares disponibles para este libro");
        }
        Ejemplar ejemplar = disponibles.get(0);

        if (lector.isBloqueado()){throw new ConflictoEstadoException("Lector bloqueado");}

        ejemplar.setEstado(cl.ucn.app.model.biblioteca.EstadoEjemplar.PRESTADO);
        PrestamoLibro ejemplarPrestado = new PrestamoLibro();
        ejemplarPrestado.setFechaInicio(LocalDate.now());
        ejemplarPrestado.setFechaVencimiento(fechaVencimiento);
        ejemplarPrestado.setEstado(cl.ucn.app.model.biblioteca.EstadoPrestamo.ACTIVO);
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

    public void confirmarEntrega(Long prestamoId) {
        PrestamoLibro prestamo = prestamoLibroRepository.findById(prestamoId);
        if (prestamo == null) {
            throw new RecursoNoEncontradoException("Préstamo no encontrado");
        }
        if (prestamo.getEstado() != cl.ucn.app.model.biblioteca.EstadoPrestamo.SOLICITADO) {
            throw new ConflictoEstadoException("Solo se pueden entregar libros en estado SOLICITADO");
        }
        prestamo.setEstado(cl.ucn.app.model.biblioteca.EstadoPrestamo.ACTIVO);
        prestamo.setFechaInicio(LocalDate.now());
        prestamoLibroRepository.save(prestamo);
    }

    public void solicitarDevolucion(Long prestamoId) {
        PrestamoLibro prestamo = prestamoLibroRepository.findById(prestamoId);
        if (prestamo == null) {
            throw new RecursoNoEncontradoException("Préstamo no encontrado");
        }
        if (prestamo.getEstado() != cl.ucn.app.model.biblioteca.EstadoPrestamo.ACTIVO) {
            throw new ConflictoEstadoException("Solo se puede solicitar devolución de préstamos activos");
        }
        prestamo.setEstado(cl.ucn.app.model.biblioteca.EstadoPrestamo.PENDIENTE_DEVOLUCION);
        prestamoLibroRepository.save(prestamo);
    }

    public void confirmarDevolucion(Long prestamoId) {
        new DevolucionService().registrarDevolucion(prestamoId);
    }
}
