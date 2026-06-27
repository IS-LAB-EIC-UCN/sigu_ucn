package cl.ucn.app.service.biblioteca;

import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Ejemplar;

import java.time.LocalDate;
public class PrestamoLibroService {
    private final LectorRepository lectorRepository;
    private final EjemplarRepository ejemplarRepository;
    private final PrestamoLibroRepository prestamoLibroRepository;

    public PrestamoLibroService(){
        this.lectorRepository = new LectorRepository();
        this.ejemplarRepository = new EjemplarRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
    }
    //para Testing
    PrestamoLibroService(PrestamoLibroRepository prestamoLibroRepository,
                         LectorRepository lectorRepository,
                         EjemplarRepository ejemplarRepository) {
        this.prestamoLibroRepository = prestamoLibroRepository;
        this.lectorRepository = lectorRepository;
        this.ejemplarRepository = ejemplarRepository;
    }

    public PrestamoLibro solicitarPrestamo(Long lectorId, Long ejemplarId, LocalDate fechaVencimiento){

        if (fechaVencimiento == null || fechaVencimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser nula ni anterior a hoy");
        }
        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null) {throw new IllegalArgumentException("ID de lector" + lectorId +" inexistente");}

        Ejemplar ejemplar = ejemplarRepository.findById(ejemplarId);
        if (ejemplar == null){throw new IllegalArgumentException("Ejemplar inexistente");}

        if (!ejemplar.getEstado().equalsIgnoreCase("DISPONIBLE")){
            throw new IllegalArgumentException("Ejemplar no disponible, por favor elija otro");
        }

        if (lector.isBloqueado()){throw new IllegalArgumentException("Lector bloqueado");}

        ejemplar.setEstado("PRESTADO");
        ejemplarRepository.save(ejemplar);
        PrestamoLibro ejemplarPrestado = new PrestamoLibro();
        ejemplarPrestado.setFechaInicio(LocalDate.now());
        ejemplarPrestado.setFechaVencimiento(fechaVencimiento);
        ejemplarPrestado.setEstado("ACTIVO");
        ejemplarPrestado.setLector(lector);
        ejemplarPrestado.setEjemplar(ejemplar);

        prestamoLibroRepository.save(ejemplarPrestado);

        return ejemplarPrestado;

    }
}
