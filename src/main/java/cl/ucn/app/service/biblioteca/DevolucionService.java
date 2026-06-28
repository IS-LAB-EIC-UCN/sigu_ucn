package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.exceptions.ConflictoEstadoException;
import cl.ucn.app.exceptions.RecursoNoEncontradoException;
import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.IPrestamoLibroRepository;
import cl.ucn.app.service.biblioteca.api.IDevolucionService;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DevolucionService implements IDevolucionService {
    private final IEjemplarRepository ejemplarRepository;
    private final IPrestamoLibroRepository prestamoLibroRepository;
    private final MultaService multaService;

    public DevolucionService(){
        this.ejemplarRepository = new EjemplarRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
        this.multaService = new MultaService();
    }

    public DevolucionService(IEjemplarRepository ejemplarRepository,
                             IPrestamoLibroRepository prestamoLibroRepository,
                             MultaService multaService) {
        this.ejemplarRepository = ejemplarRepository;
        this.prestamoLibroRepository = prestamoLibroRepository;
        this.multaService = multaService;
    }

    public void registrarDevolucion(long prestamoId){
        PrestamoLibro prestamo = prestamoLibroRepository.findById(prestamoId);
        if (prestamo == null){throw new RecursoNoEncontradoException("Prestamo no existe");}
        if (prestamo.getEstado().equalsIgnoreCase("FINALIZADO")){throw new ConflictoEstadoException("Este prestamo ya fue finalizado");}

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = prestamo.getFechaVencimiento();

        if (vencimiento != null && vencimiento.isBefore(hoy)){
            long atraso = ChronoUnit.DAYS.between(vencimiento, hoy);
            int diasAtraso = (int) atraso;

            if (diasAtraso > 0){
                try (EntityManager em = JPAUtil.getEntityManager()) {
                    try {
                        em.getTransaction().begin();
                        multaService.generarMulta(prestamo, diasAtraso);
                        em.getTransaction().commit();
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        throw e;
                    }
                }
            }
        }

        Ejemplar ejemplar = prestamo.getEjemplar();
        if (ejemplar != null){
            ejemplar.setEstado("DISPONIBLE");
        }

        prestamo.setEstado("FINALIZADO");
        prestamo.setFechaDevolucion(LocalDate.now());

        try (EntityManager em = JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                if (ejemplar != null) {
                    ejemplarRepository.save(ejemplar, em);
                }
                prestamoLibroRepository.save(prestamo, em);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }

    }
}
