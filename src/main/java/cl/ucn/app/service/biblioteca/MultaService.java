package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;

public class MultaService {
    private final MultaRepository multaRepository;
    private final LectorRepository lectorRepository;
    private final LectorService lectorService;

    public MultaService(){
        this.multaRepository = new MultaRepository();
        this.lectorRepository = new LectorRepository();
        this.lectorService = new LectorService();
    }


    public Multa generarMulta(PrestamoLibro prestamo, int diasAtraso) {
        if (prestamo == null || diasAtraso <= 0){return null;}

        double montoAtrasado = diasAtraso * 1000;

        Multa multa = new Multa();
        multa.setDiasAtraso(diasAtraso);
        multa.setMonto(montoAtrasado);
        multa.setPrestamo(prestamo);

        multaRepository.save(multa);

        Lector lector = prestamo.getLector();
        if (lector != null){
            lector.setBloqueado(true);
            lectorRepository.save(lector);
        }
        return multa;
    }

    public void registrarPago(Long multaId){
        Multa multa = multaRepository.findById(multaId);

        if (multa != null && !multa.getPagada()){
            multa.setPagada(true);
            multaRepository.save(multa);

            Lector lector = multa.getPrestamo().getLector();
            if (lector != null){
                boolean tieneDeudas = lectorService.tieneDeudaPendiente(lector.getId());

                if(!tieneDeudas){
                    lector.setBloqueado(false);
                    lectorRepository.save(lector);
                }
            }
        }
    }
}
