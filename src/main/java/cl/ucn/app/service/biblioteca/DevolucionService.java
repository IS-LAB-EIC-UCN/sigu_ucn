package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.EstadoEjemplar;
import cl.ucn.app.model.biblioteca.EstadoPrestamo;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.IPrestamoLibroRepository;
import cl.ucn.app.service.biblioteca.api.IDevolucionService;
import cl.ucn.app.service.biblioteca.api.IMultaService;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DevolucionService implements IDevolucionService {
    private final IEjemplarRepository ejemplarRepository;
    private final IPrestamoLibroRepository prestamoLibroRepository;
    private final IMultaService multaService;

    public DevolucionService() {
        this.ejemplarRepository = new EjemplarRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
        this.multaService = new MultaService();
    }

    public DevolucionService(IEjemplarRepository ejemplarRepository,
                             IPrestamoLibroRepository prestamoLibroRepository,
                             IMultaService multaService) {
        this.ejemplarRepository = ejemplarRepository;
        this.prestamoLibroRepository = prestamoLibroRepository;
        this.multaService = multaService;
    }

    public void registrarDevolucion(long prestamoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            registrarDevolucion(prestamoId, em);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void registrarDevolucion(long prestamoId, EntityManager em) {
        PrestamoLibro prestamo = prestamoLibroRepository.findById(prestamoId);
        if (prestamo == null) {
            throw new RecursoNoEncontradoException("Prestamo no existe");
        }
        if (prestamo.getEstado() == EstadoPrestamo.FINALIZADO) {
            throw new ConflictoEstadoException("Este prestamo ya fue finalizado");
        }

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = prestamo.getFechaVencimiento();

        if (vencimiento != null && vencimiento.isBefore(hoy)) {
            long atraso = ChronoUnit.DAYS.between(vencimiento, hoy);
            int diasAtraso = (int) atraso;
            if (diasAtraso > 0) {
                multaService.generarMulta(prestamo, diasAtraso, em);
            }
        }

        Ejemplar ejemplar = prestamo.getEjemplar();
        if (ejemplar != null) {
            ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
            ejemplarRepository.save(ejemplar, em);
        }

        prestamo.setEstado(EstadoPrestamo.FINALIZADO);
        prestamo.setFechaDevolucion(LocalDate.now());
        prestamoLibroRepository.save(prestamo, em);
    }
}
