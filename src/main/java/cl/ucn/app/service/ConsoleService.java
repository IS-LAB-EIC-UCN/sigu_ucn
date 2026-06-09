package cl.ucn.app.service;

import java.util.List;

import cl.ucn.app.model.Notificacion;
import cl.ucn.app.model.Salida;
import cl.ucn.app.repository.RecursoRepository;
import cl.ucn.app.service.Interfaces.IConsole;

/**
 * Debiera enviar a la capa de Navegador (...)
 */
public class ConsoleService implements IConsole {

    private final RecursoRepository recursoRepository;
    private final NotificacionService notificador;
    private final FiltradoCategoriaService filtrador;
    private final CheckerService checker;

    public ConsoleService() {
        this.recursoRepository = new RecursoRepository();
        this.notificador = new NotificacionService();
        this.filtrador = new FiltradoCategoriaService();
        this.checker = new CheckerService();
    }

    @Override
    public void log(Object salida) {
        System.out.println(salida);
    }

    @Override
    public void log(String salida) {
        System.out.println(salida);
    }

    @Override
    public void log(List<Object> salida) {
        for (Object o: salida) {
            System.out.println(o.toString());
        }
    }

    @Override
    public void log_inventory(String categoria) {
        if (categoria == null) {
            System.out.println(recursoRepository.findAll());
            return;
        }
        if (checker.validar_string(categoria)) {
            filtrador.filtrar(categoria);
        }
    }

    @Override
    public void log_alerta(Salida salida) {
        String mensajep1 = String.format(
            "[Aviso]: el insumo %s tiene un stock menor al minimo (%d)", 
            salida.getRecurso().getNombre(), salida.getRecurso().getStock()
        );
        String mensajep2 = String.format("\n %s", salida.toString());
        
        notificador.setNotificacion(new Notificacion(mensajep1+mensajep2));
        notificador.mostrar();
    }
    
}
