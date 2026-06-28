package cl.ucn.app.service;

import java.time.LocalDate;
import java.util.Scanner;

import cl.ucn.app.service.Interfaces.INormal;

public class NormalService implements INormal {

    private final ConsoleService console;
    private final CheckerService checker;
    private final Scanner scanner;
    private final PrestamoService prestamoService;

    public NormalService() {
        this.console = new ConsoleService();
        this.checker = new CheckerService();
        this.scanner = new Scanner(System.in);
        this.prestamoService = new PrestamoService();
    }

    public NormalService(ConsoleService console, CheckerService checker,
                         PrestamoService prestamoService, Scanner scanner) {
        this.console = console;
        this.checker = checker;
        this.prestamoService = prestamoService;
        this.scanner = scanner;
    }

    @Override
    public void pedirPrestamo(Long usuario_id) {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz
        console.log("_Pedir Prestamo_");

        console.log("id del Recurso (Equipo):");
        String equipo_id_str = scanner.nextLine();

        if(!checker.validar_num(equipo_id_str)) return;
        Long equipo_id = Long.parseLong(equipo_id_str);

        console.log("cantidad del Recurso (Equipo):");
        String cantidad_str = scanner.nextLine();

        if(!checker.validar_num(equipo_id_str)) return;
        int cantidad = Integer.parseInt(equipo_id_str);

        prestamoService.pedirPrestamo(equipo_id, cantidad, usuario_id);
    }

    @Override
    public void devolverEquipo(Long usuario_id, Long movimiento_id) {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz
        console.log("_Devolver Equipo_");

        console.log("id del Recurso (Equipo):");
        String equipo_id_str = scanner.nextLine();

        if(!checker.validar_num(equipo_id_str)) return;
        Long equipo_id = Long.parseLong(equipo_id_str);

        prestamoService.devolverEquipo(equipo_id, movimiento_id, usuario_id);
    }
    
}
