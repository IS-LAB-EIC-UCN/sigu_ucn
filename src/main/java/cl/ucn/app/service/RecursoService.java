package cl.ucn.app.service;

import cl.ucn.app.model.Recurso;
import cl.ucn.app.repository.RecursoRepository;

public class RecursoService {

    private final RecursoRepository recursoRepository;
    private static RecursoFactoryService recFactory;

    public RecursoService() {
        this.recursoRepository = new RecursoRepository();
        recFactory = new RecursoFactoryService();
    }

    public boolean crearRecurso(String nombre, int stock, String tipo) {
        Recurso recurso = null;

        if (tipo.equals("INSUMO")) recurso = recFactory.crearInsumo(nombre, stock);
        if (tipo.equals("EQUIPO")) recurso = recFactory.crearEquipo(nombre, stock);

        recursoRepository.save(recurso);
        return true;
    }
}
