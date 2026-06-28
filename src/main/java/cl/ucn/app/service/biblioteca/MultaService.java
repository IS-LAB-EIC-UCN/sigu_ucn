package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.repository.biblioteca.api.ILectorRepository;
import cl.ucn.app.repository.biblioteca.api.IMultaRepository;
import cl.ucn.app.service.biblioteca.api.IMultaService;
import cl.ucn.app.service.biblioteca.api.ILectorService;
import cl.ucn.app.service.biblioteca.strategy.ICalculadorMulta;
import cl.ucn.app.service.biblioteca.strategy.MultaLinealPorDias;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MultaService implements IMultaService {
    private final IMultaRepository multaRepository;
    private final ILectorRepository lectorRepository;
    private final ILectorService lectorService;
    private final ICalculadorMulta calculador;

    public MultaService(){
        this.multaRepository = new MultaRepository();
        this.lectorRepository = new LectorRepository();
        this.lectorService = new LectorService();
        this.calculador = new MultaLinealPorDias();
    }

    public MultaService(IMultaRepository multaRepository, ILectorRepository lectorRepository) {
        this.multaRepository = multaRepository;
        this.lectorRepository = lectorRepository;
        this.lectorService = new LectorService();
        this.calculador = new MultaLinealPorDias();
    }

    public MultaService(IMultaRepository multaRepository, ILectorRepository lectorRepository, ILectorService lectorService) {
        this.multaRepository = multaRepository;
        this.lectorRepository = lectorRepository;
        this.lectorService = lectorService;
        this.calculador = new MultaLinealPorDias();
    }

    public MultaService(IMultaRepository multaRepository, ILectorRepository lectorRepository, ICalculadorMulta calculador) {
        this.multaRepository = multaRepository;
        this.lectorRepository = lectorRepository;
        this.lectorService = new LectorService();
        this.calculador = calculador;
    }

    public MultaService(IMultaRepository multaRepository, ILectorRepository lectorRepository, ILectorService lectorService, ICalculadorMulta calculador) {
        this.multaRepository = multaRepository;
        this.lectorRepository = lectorRepository;
        this.lectorService = lectorService;
        this.calculador = calculador;
    }

    public Multa generarMulta(PrestamoLibro prestamo, int diasAtraso, EntityManager em) {
        if (prestamo == null || diasAtraso <= 0){return null;}

        BigDecimal montoAtrasado = calculador.calcular(diasAtraso);

        Multa multa = new Multa();
        multa.setDiasAtraso(diasAtraso);
        multa.setMonto(montoAtrasado);
        multa.setFechaGeneracion(LocalDate.now());
        multa.setPrestamo(prestamo);

        Lector lector = prestamo.getLector();
        if (lector != null){
            lector.setBloqueado(true);
        }

        multaRepository.save(multa, em);
        if (lector != null) {
            lectorRepository.save(lector, em);
        }

        return multa;
    }

    public Multa generarMulta(PrestamoLibro prestamo, int diasAtraso) {
        if (prestamo == null || diasAtraso <= 0){return null;}

        try (EntityManager em = JPAUtil.getEntityManager()) {
            try {
                em.getTransaction().begin();
                Multa m = generarMulta(prestamo, diasAtraso, em);
                em.getTransaction().commit();
                return m;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }

    public void registrarPago(Long multaId){
        Multa multa = multaRepository.findById(multaId);

        if (multa != null && !multa.getPagada()){
            multa.setPagada(true);

            Lector lector = multa.getPrestamo().getLector();
            if (lector != null){
                boolean tieneDeudas = lectorService.tieneDeudaPendiente(lector.getId());
                if(!tieneDeudas){
                    lector.setBloqueado(false);
                }
            }

            try (EntityManager em = JPAUtil.getEntityManager()) {
                try {
                    em.getTransaction().begin();
                    multaRepository.save(multa, em);
                    if (lector != null) {
                        lectorRepository.save(lector, em);
                    }
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
}
