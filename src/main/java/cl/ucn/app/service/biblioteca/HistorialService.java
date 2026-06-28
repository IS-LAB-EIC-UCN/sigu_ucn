package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.LectorRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.api.ILectorRepository;
import cl.ucn.app.repository.biblioteca.api.IPrestamoLibroRepository;
import cl.ucn.app.service.biblioteca.api.IHistorialService;

import java.util.ArrayList;
import java.util.List;

public class HistorialService implements IHistorialService {
    private final ILectorRepository lectorRepository;
    private final IPrestamoLibroRepository prestamoLibroRepository;

    public HistorialService(){
        this.lectorRepository = new LectorRepository();
        this.prestamoLibroRepository = new PrestamoLibroRepository();
    }

    public HistorialService(ILectorRepository lectorRepository, IPrestamoLibroRepository prestamoLibroRepository) {
        this.lectorRepository = lectorRepository;
        this.prestamoLibroRepository = prestamoLibroRepository;
    }

    public List<PrestamoLibro> obtenerHistorial(Long lectorId){
        if (lectorId == null){return new ArrayList<>();}

        Lector lector = lectorRepository.findById(lectorId);
        if (lector == null){throw new cl.ucn.app.exceptions.RecursoNoEncontradoException("ID de lector no existe");}

        return prestamoLibroRepository.findByLector(lector);
    }

    public List<PrestamoLibro> obtenerTodosLosPrestamos() {
        return prestamoLibroRepository.findAll();
    }
}
