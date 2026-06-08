package cl.ucn.app.service;

import java.util.Scanner;

import cl.ucn.app.model.Recurso;
import cl.ucn.app.repository.PrestamoRepository;
import cl.ucn.app.repository.RecursoRepository;
import cl.ucn.app.service.Interfaces.INormal;

public class NormalService implements INormal {

    private final ConsoleService console;
    private final CheckerService checker;
    private final RecursoRepository recursoRepository;
    private final PrestamoRepository prestamoRepository;
    private Scanner scanner;

    public NormalService() {
        this.console = new ConsoleService();
        this.checker = new CheckerService();
        this.recursoRepository = new RecursoRepository();
        this.prestamoRepository = new PrestamoRepository();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void pedirPrestamo() {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz
        console.log("_Pedir Prestamo_");

        console.log("id del Recurso (Equipo):");
        String equipo_id_str = scanner.nextLine();

        if(!checker.validar_num(equipo_id_str)) return;
        Long equipo_id = Long.parseLong(equipo_id_str);

        Recurso recurso = recursoRepository.findById(equipo_id);

        prestamoRepository.alter(equipo_id, "PRESTAMO PENDIENTE");
    }

    @Override
    public void devolverEquipo() {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz
        console.log("_Devolver Equipo_");

        console.log("id del Recurso (Equipo):");
        String equipo_id_str = scanner.nextLine();

        if(!checker.validar_num(equipo_id_str)) return;
        Long equipo_id = Long.parseLong(equipo_id_str);

        prestamoRepository.alter(equipo_id, "DEVOLUCION PENDIENTE");

    }
    
}
