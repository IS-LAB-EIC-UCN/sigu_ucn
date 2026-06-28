package cl.ucn.app.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

import cl.ucn.app.model.Rol;
import cl.ucn.app.repository.DB_local;
import cl.ucn.app.service.Interfaces.IAdmin;

public class AdminService implements IAdmin {

    private final ConsoleService console;
    private final CheckerService checker;
    private final Scanner scanner;
    private final PrestamoService prestamoService;
    private final StockService stockService;

    public AdminService() {
        this.console = new ConsoleService();
        this.checker = new CheckerService();
        this.scanner = new Scanner(System.in);
        this.prestamoService = new PrestamoService();
        this.stockService = new StockService();
    }

    @Override
    public void registrarRecurso() {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz
        console.log("_Registrar un Recurso_");

        console.log("Nombre:");
        String nombre = scanner.nextLine();

        if(!checker.validar_string(nombre)) return;

        console.log("Stock inicial:");
        String stock_str = scanner.nextLine();

        if(!checker.validar_num(stock_str)) return;
        int stock = Integer.parseInt(stock_str);

        console.log("Tipo (Insumo o equipo):");
        String tipo = scanner.nextLine();

        if(!checker.validar_string(tipo)) return;
        tipo = tipo.toUpperCase();

        DB_local.addRecurso(nombre, stock, tipo);
        return;
    }

    @Override
    public void registrarUsuario() {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz
        console.log("_Registrar un Usuario_");

        console.log("Nombre:");
        String nombre = scanner.nextLine();

        if(!checker.validar_string(nombre)) return;

        console.log("Correo:");
        String correo = scanner.nextLine();

        if(!checker.validar_correo(correo)) return;

        console.log("Password:");
        String password = scanner.nextLine();

        console.log("Activo");
        String activo_str = scanner.nextLine();

        //->...
        Boolean activo;
        if (activo_str.equalsIgnoreCase("TRUE")) activo = true;
        if (activo_str.equalsIgnoreCase("FALSE")) activo = false;
        else return;

        console.log("Rol");
        String nombre_rol = scanner.nextLine();

        Rol roltemp = new Rol(nombre_rol);
        DB_local.addUsuario(nombre, correo, password, activo, roltemp);
        roltemp = null;
    }

    @Override
    public void registrarProveedor() {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz

        console.log("_Registrar un Proveedor_");

        console.log("Nombre:");
        String nombre = scanner.nextLine();

        if(!checker.validar_string(nombre)) return;

        console.log("Correo:");
        String correo = scanner.nextLine();

        if(!checker.validar_correo(correo)) return;

        console.log("Telefono (opcional):");
        String telefono = scanner.nextLine();

        if(!checker.validar_telefono(telefono)) return;

        DB_local.addProveedor(nombre, correo, telefono);
    }

    @Override
    public void registrarMovimiento() {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz

        console.log("_Registrar un Movimiento de Inventario_");

        //->...
        console.log("Tipo de Movimiento (Entrada, Salida, Prestamo o Devolucion):");
        String tipo = scanner.nextLine();

        Long id_recurso = (long) -1L;
        Long proveedor_id = -1L;
        Long usuario_id = -1L;
        int cantidad = -1;
        LocalDate fecha = null;
        LocalTime hora = null;
        String estado = null;

        //->...
        console.log("id del Recurso:");
        String id_recurso_str = scanner.nextLine();

        if(!checker.validar_num(id_recurso_str)) return;
        id_recurso = Long.parseLong(id_recurso_str);

        console.log("Cantidad:");
        String cantidad_str = scanner.nextLine();

        if(!checker.validar_num(cantidad_str)) return;
        cantidad = Integer.parseInt(cantidad_str);

        console.log("Fecha:");
        String fecha_str = scanner.nextLine();

        if(!checker.validar_fecha(fecha_str)) return;
        fecha = LocalDate.parse(fecha_str);

        console.log("Hora:");
        String hora_str = scanner.nextLine();

        if(!checker.validar_hora(hora_str)) return;
        hora = LocalTime.parse(hora_str);

        //->...
        if (tipo.equalsIgnoreCase("ENTRADA")) {
            console.log("id del Proveedor:");
            String id_proveedor_str = scanner.nextLine();

            if(!checker.validar_num(id_proveedor_str)) return;
            proveedor_id = Long.parseLong(id_proveedor_str);

            stockService.crearEntrada(id_recurso, cantidad, fecha, hora, proveedor_id);
            return;

        } else if (tipo.equalsIgnoreCase("PRESTAMO")) {
            console.log("id del Usuario:");
            String id_usuario_str = scanner.nextLine();

            if(!checker.validar_num(id_usuario_str)) return;
            usuario_id = Long.parseLong(id_usuario_str);

            console.log("Estado:");
            estado = scanner.nextLine();
        }

        if (tipo.equalsIgnoreCase("PRESTAMO") || tipo.equalsIgnoreCase("DEVOLUCION")) {
            console.log("¿Agregar o Editar?:");

            String eleccion = scanner.nextLine();

            if (eleccion.equalsIgnoreCase("EDITAR")) {
                console.log("id del Movimiento:");
                String id_movimiento_str = scanner.nextLine();

                if(!checker.validar_num(id_movimiento_str)) return;
                Long movimiento_id = Long.parseLong(id_movimiento_str);

                prestamoService.editarPrestamo(movimiento_id, estado);
                return;
            }
            //Agregar
            prestamoService.crearPrestamo(tipo,id_recurso, cantidad, fecha, hora, usuario_id, estado);
            return;
        }
        //Si es una SALIDA:
        stockService.crearSalida(id_recurso, cantidad, fecha, hora);
    }
    
}
