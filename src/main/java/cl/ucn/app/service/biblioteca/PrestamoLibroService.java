package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.exceptions.ValidacionException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.EstadoEjemplar;
import cl.ucn.app.model.biblioteca.EstadoPrestamo;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.ILectorRepository;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import cl.ucn.app.repository.biblioteca.api.IPrestamoLibroRepository;
import cl.ucn.app.service.biblioteca.api.IDevolucionService;
import cl.ucn.app.service.biblioteca.api.IPrestamoService;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class PrestamoLibroService implements IPrestamoService {
    private final ILectorRepository lectorRepository;
    private final IEjemplarRepository ejemplarRepository;
    private final IPrestamoLibroRepository prestamoLibroRepository;
    private final ILibroRepository libroRepository;
    private final IDevolucionService devolucionService;

    public PrestamoLibroService() {
        this.lectorRepository = new LectorRepository();
        this.ejemplarRepository = new EjemplarRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
        this.libroRepository = new LibroRepository();
        this.devolucionService = new DevolucionService();
    }

    public PrestamoLibroService(IPrestamoLibroRepository prestamoLibroRepository,
                                ILectorRepository lectorRepository,
                                IEjemplarRepository ejemplarRepository,
                                ILibroRepository libroRepository) {
        this(prestamoLibroRepository, lectorRepository, ejemplarRepository, libroRepository, new DevolucionService());
    }

    public PrestamoLibroService(IPrestamoLibroRepository prestamoLibroRepository,
                                ILectorRepository lectorRepository,
                                IEjemplarRepository ejemplarRepository,
                                ILibroRepository libroRepository,
                                IDevolucionService devolucionService) {
        this.prestamoLibroRepository = prestamoLibroRepository;
        this.lectorRepository = lectorRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.libroRepository = libroRepository;
        this.devolucionService = devolucionService;
    }

    public PrestamoLibro solicitarPrestamo(Long lectorId, Long libroId, LocalDate fechaVencimiento) {
        PrestamoLibro prestamo = prepararPrestamo(lectorId, libroId, fechaVencimiento, EstadoPrestamo.SOLICITADO);
        persistirPrestamo(prestamo);
        return prestamo;
    }

    public PrestamoLibro solicitarPrestamoActivo(Long lectorId, Long libroId, LocalDate fechaVencimiento) {
        PrestamoLibro prestamo = prepararPrestamo(lectorId, libroId, fechaVencimiento, EstadoPrestamo.ACTIVO);
        persistirPrestamo(prestamo);
        return prestamo;
    }

    public PrestamoLibro prepararPrestamo(Long lectorId, Long libroId, LocalDate fechaVencimiento, EstadoPrestamo estadoInicial) {
        if (fechaVencimiento == null || fechaVencimiento.isBefore(LocalDate.now())) {
            throw new ValidacionException("La fecha de vencimiento no puede ser nula ni anterior a hoy");
        }
        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null) {
            throw new RecursoNoEncontradoException("ID de lector " + lectorId + " inexistente");
        }

        Libro libro = libroRepository.findById(libroId);
        if (libro == null) {
            throw new RecursoNoEncontradoException("Libro no existe");
        }

        List<Ejemplar> disponibles = ejemplarRepository.findDisponiblesByLibro(libro);
        if (disponibles.isEmpty()) {
            throw new ConflictoEstadoException("No hay ejemplares disponibles para este libro");
        }
        Ejemplar ejemplar = disponibles.get(0);

        if (lector.isBloqueado()) {
            throw new ConflictoEstadoException("Lector bloqueado");
        }

        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setFechaInicio(LocalDate.now());
        prestamo.setFechaVencimiento(fechaVencimiento);
        prestamo.setEstado(estadoInicial);
        prestamo.setLector(lector);
        prestamo.setEjemplar(ejemplar);
        prestamo.getEjemplar().setEstado(EstadoEjemplar.PRESTADO);
        return prestamo;
    }

    public void persistirPrestamo(PrestamoLibro prestamo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            persistirPrestamo(prestamo, em);
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

    public void persistirPrestamo(PrestamoLibro prestamo, EntityManager em) {
        if (prestamo == null) {
            throw new ValidacionException("El prestamo no puede ser nulo");
        }
        if (prestamo.getEjemplar() == null) {
            throw new ValidacionException("El prestamo debe tener un ejemplar");
        }
        ejemplarRepository.save(prestamo.getEjemplar(), em);
        prestamoLibroRepository.save(prestamo, em);
    }

    private PrestamoLibro solicitarPrestamoEnEstado(Long lectorId, Long libroId, LocalDate fechaVencimiento, EstadoPrestamo estadoInicial) {
        if (fechaVencimiento == null || fechaVencimiento.isBefore(LocalDate.now())) {
            throw new ValidacionException("La fecha de vencimiento no puede ser nula ni anterior a hoy");
        }
        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null) {
            throw new RecursoNoEncontradoException("ID de lector " + lectorId + " inexistente");
        }

        Libro libro = libroRepository.findById(libroId);
        if (libro == null) {
            throw new RecursoNoEncontradoException("Libro no existe");
        }

        List<Ejemplar> disponibles = ejemplarRepository.findDisponiblesByLibro(libro);
        if (disponibles.isEmpty()) {
            throw new ConflictoEstadoException("No hay ejemplares disponibles para este libro");
        }
        Ejemplar ejemplar = disponibles.get(0);

        if (lector.isBloqueado()) {
            throw new ConflictoEstadoException("Lector bloqueado");
        }

        ejemplar.setEstado(EstadoEjemplar.PRESTADO);
        PrestamoLibro prestamo = new PrestamoLibro();
        prestamo.setFechaInicio(LocalDate.now());
        prestamo.setFechaVencimiento(fechaVencimiento);
        prestamo.setEstado(estadoInicial);
        prestamo.setLector(lector);
        prestamo.setEjemplar(ejemplar);

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ejemplarRepository.save(ejemplar, em);
            prestamoLibroRepository.save(prestamo, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }

        return prestamo;
    }

    public void confirmarEntrega(Long prestamoId) {
        PrestamoLibro prestamo = prestamoLibroRepository.findById(prestamoId);
        if (prestamo == null) {
            throw new RecursoNoEncontradoException("Préstamo no encontrado");
        }
        if (prestamo.getEstado() != EstadoPrestamo.SOLICITADO) {
            throw new ConflictoEstadoException("Solo se pueden entregar libros en estado SOLICITADO");
        }
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        prestamo.setFechaInicio(LocalDate.now());
        prestamoLibroRepository.save(prestamo);
    }

    public void solicitarDevolucion(Long prestamoId) {
        PrestamoLibro prestamo = prestamoLibroRepository.findById(prestamoId);
        if (prestamo == null) {
            throw new RecursoNoEncontradoException("Préstamo no encontrado");
        }
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new ConflictoEstadoException("Solo se puede solicitar devolución de préstamos activos");
        }
        prestamo.setEstado(EstadoPrestamo.PENDIENTE_DEVOLUCION);
        prestamoLibroRepository.save(prestamo);
    }

    public void confirmarDevolucion(Long prestamoId) {
        devolucionService.registrarDevolucion(prestamoId);
    }
}
