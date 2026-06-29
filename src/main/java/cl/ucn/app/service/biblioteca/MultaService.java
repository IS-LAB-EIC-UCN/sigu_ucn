package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.repository.biblioteca.api.ILectorRepository;
import cl.ucn.app.repository.biblioteca.api.IMultaRepository;
import cl.ucn.app.service.biblioteca.api.ILectorService;
import cl.ucn.app.service.biblioteca.api.IMultaService;
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

    public MultaService() {
        this(new MultaRepository(), new LectorRepository(), new LectorService(), new MultaLinealPorDias());
    }

    public MultaService(IMultaRepository multaRepository, ILectorRepository lectorRepository) {
        this(multaRepository, lectorRepository, new LectorService(), new MultaLinealPorDias());
    }

    public MultaService(IMultaRepository multaRepository, ILectorRepository lectorRepository,
                        ILectorService lectorService) {
        this(multaRepository, lectorRepository, lectorService, new MultaLinealPorDias());
    }

    public MultaService(IMultaRepository multaRepository, ILectorRepository lectorRepository,
                        ILectorService lectorService, ICalculadorMulta calculador) {
        this.multaRepository = multaRepository;
        this.lectorRepository = lectorRepository;
        this.lectorService = lectorService;
        this.calculador = calculador;
    }

    public Multa generarMulta(PrestamoLibro prestamo, int diasAtraso) {
        return generarMulta(prestamo, diasAtraso, (EntityManager) null);
    }

    public Multa generarMulta(PrestamoLibro prestamo, int diasAtraso, EntityManager em) {
        if (prestamo == null || diasAtraso <= 0) {
            return null;
        }

        BigDecimal montoAtrasado = calculador.calcular(diasAtraso);

        Multa multa = new Multa();
        multa.setDiasAtraso(diasAtraso);
        multa.setMonto(montoAtrasado);
        multa.setFechaGeneracion(LocalDate.now());
        multa.setPagada(false);
        multa.setPrestamo(prestamo);

        Lector lector = prestamo.getLector();
        if (lector != null) {
            lector.setBloqueado(true);
        }

        if (em == null) {
            multaRepository.save(multa);
            if (lector != null) {
                lectorRepository.save(lector);
            }
        } else {
            multaRepository.save(multa, em);
            if (lector != null) {
                lectorRepository.save(lector, em);
            }
        }

        return multa;
    }

    public void registrarPago(Long multaId) {
        Multa multa = multaRepository.findById(multaId);
        if (multa == null || multa.getPagada()) {
            return;
        }
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();
            try {
                registrarPago(multa, em);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }

    public void registrarPago(Multa multa, EntityManager em) {
        if (multa == null || multa.getPagada()) {
            return;
        }
        multa.setPagada(true);

        Lector lector = multa.getPrestamo() != null ? multa.getPrestamo().getLector() : null;
        if (lector != null) {
            boolean tieneDeudas = lectorService.tieneDeudaPendiente(lector.getId());
            if (!tieneDeudas) {
                lector.setBloqueado(false);
            }
        }

        multaRepository.save(multa, em);
        if (lector != null) {
            lectorRepository.save(lector, em);
        }
    }
}
