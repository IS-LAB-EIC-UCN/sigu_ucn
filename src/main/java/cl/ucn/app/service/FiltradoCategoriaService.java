package cl.ucn.app.service;

import java.util.List;

import cl.ucn.app.model.Recurso;
import cl.ucn.app.repository.RecursoRepository;
import cl.ucn.app.service.Interfaces.IConsole;
import cl.ucn.app.service.Interfaces.IEstrategiaFiltrado;

public class FiltradoCategoriaService implements IEstrategiaFiltrado {

    private final RecursoRepository recursoRepository;
    private final IConsole console;

    public FiltradoCategoriaService (IConsole console) {
        this.recursoRepository = new RecursoRepository();
        this.console = console;
    }

    @Override
    public void filtrar(String categoria) {
        List<Recurso> filtrado = recursoRepository.findByCategoria(categoria);
        //enviar a capa de navegador
        console.log(filtrado);
    }
    
}
