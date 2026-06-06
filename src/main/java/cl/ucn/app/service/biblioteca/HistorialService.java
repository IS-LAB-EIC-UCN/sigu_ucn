package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;

import java.util.ArrayList;
import java.util.List;

public class HistorialService {
    private final LectorRepository lectorRepository;
    private final PrestamoLibroRepository prestamoLibroRepository;

    public HistorialService(){
        this.lectorRepository = new LectorRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
    }

    public List<PrestamoLibro> obtenerHistorial(Long lectorId){
        if (lectorId == null){return new ArrayList<>();}

        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null){throw new IllegalArgumentException("ID de lector no existe");}

        return prestamoLibroRepository.findByLector(lector);
    }
}
