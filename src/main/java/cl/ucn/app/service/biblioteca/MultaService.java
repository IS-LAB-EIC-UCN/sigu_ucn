package cl.ucn.app.service.biblioteca;

import cl.ucn.app.config.JPAUtil;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class MultaService {
    private final MultaRepository multaRepository;
    private final LectorRepository lectorRepository;
    private final LectorService lectorService;

    private static final BigDecimal MONTO_POR_DIA = new BigDecimal("1000");

    public MultaService(){
        this.multaRepository = new MultaRepository();
        this.lectorRepository = new LectorRepository();
        this.lectorService = new LectorService();
    }

    public Multa generarMulta(PrestamoLibro prestamo, int diasAtraso) {
        if (prestamo == null || diasAtraso <= 0){return null;}

        BigDecimal montoAtrasado = MONTO_POR_DIA
            .multiply(BigDecimal.valueOf(diasAtraso))
            .setScale(2, RoundingMode.HALF_UP);

        Multa multa = new Multa();
        multa.setDiasAtraso(diasAtraso);
        multa.setMonto(montoAtrasado);
        multa.setFechaGeneracion(LocalDate.now());
        multa.setPrestamo(prestamo);

        Lector lector = prestamo.getLector();
        if (lector != null){
            lector.setBloqueado(true);
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

        return multa;
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
