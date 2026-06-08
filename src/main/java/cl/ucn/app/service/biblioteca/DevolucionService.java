package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DevolucionService {
    private final EjemplarRepository ejemplarRepository;
    private final PrestamoLibroRepository prestamoLibroRepository;
    private final MultaService multaService;

    public DevolucionService(){
        this.ejemplarRepository = new EjemplarRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
        this.multaService = new MultaService();
    }

    public void registrarDevolucion(long prestamoId){
        PrestamoLibro prestamo = prestamoLibroRepository.findById(prestamoId);
        if (prestamo == null){throw new IllegalArgumentException("Prestamo no existe");}
        if (prestamo.getEstado().equalsIgnoreCase("FINALIZADO")){throw new IllegalArgumentException("Este prestamo ya fue finalizado");}

        LocalDate hoy = LocalDate.now();
        LocalDate vencimiento = prestamo.getFechaVencimiento();

        if (vencimiento != null && vencimiento.isBefore(hoy)){
            long atraso = ChronoUnit.DAYS.between(vencimiento, hoy);
            int diasAtraso = (int) atraso;

            if (diasAtraso > 0){multaService.generarMulta(prestamo,diasAtraso);}
        }

        Ejemplar ejemplar = prestamo.getEjemplar();
        if (ejemplar != null){
            ejemplar.setEstado("DISPONIBLE");
            ejemplarRepository.save(ejemplar);
        }

        prestamo.setEstado("FINALIZADO");
        prestamo.setFechaDevolucion(LocalDate.now());
        prestamoLibroRepository.save(prestamo);

    }
}
