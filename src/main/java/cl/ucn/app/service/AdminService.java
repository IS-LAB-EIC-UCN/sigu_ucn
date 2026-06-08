package cl.ucn.app.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

import cl.ucn.app.model.Salida;
import cl.ucn.app.repository.PrestamoRepository;
import cl.ucn.app.service.Interfaces.IAdmin;
import cl.ucn.app.service.Interfaces.IObserver;

public class AdminService implements IAdmin, IObserver {

    private final ConsoleService console;
    private final CheckerService checker;
    private final PrestamoRepository prestamoRepository;
    private Scanner scanner;

    public AdminService() {
        this.console = new ConsoleService();
        this.checker = new CheckerService();
        this.prestamoRepository = new PrestamoRepository();
        this.scanner = new Scanner(System.in);
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

        //pasar a DB_local con addRecurso(nombre, stock, tipo);

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
        if (activo_str.toUpperCase().equals("TRUE")) activo = true;
        if (activo_str.toUpperCase().equals("FALSE")) activo = false;
        else return;

        console.log("Rol");
        String nombre_rol = scanner.nextLine();

        //pasar a DB_local con addUsuario(nombre, correo, password, activo, nombre_rol);

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

        //pasar a DB_local con addProveedor(nombre, correo, telefono);
    }

    @Override
    public void registrarMovimiento() {
        //Debiera depender de la respuesta de la capa de presentacion
        //Usando servicio -> controladores -> rutas -> interfaz

        console.log("_Registrar un Movimiento de Inventario_");

        //->...
        console.log("Tipo de Movimiento (Entrada, Salida, Prestamo o Devolucion):");
        String tipo = scanner.nextLine();

        Long id_recurso = (long) -1;
        Long id_proveedor = (long) -1;
        Long id_usuario = (long) -1;
        String estado = null;

        //->...
        if (!tipo.toUpperCase().equals("DEVOLUCION")) {
            console.log("id del Recurso:");
            String id_recurso_str = scanner.nextLine();

            if(!checker.validar_num(id_recurso_str)) return;
            id_recurso = Long.parseLong(id_recurso_str);

            console.log("Cantidad:");
            String cantidad_str = scanner.nextLine();

            if(!checker.validar_num(cantidad_str)) return;
            int cantidad = Integer.parseInt(cantidad_str);

            console.log("Fecha:");
            String fecha_str = scanner.nextLine();
            
            if(!checker.validar_fecha(fecha_str)) return;
            LocalDate fecha = LocalDate.parse(fecha_str);

            console.log("Hora:");
            String hora_str = scanner.nextLine();

            if(!checker.validar_hora(hora_str)) return;
            LocalTime hora = LocalTime.parse(hora_str);

            //->...
            if (tipo.toUpperCase().equals("ENTRADA")) {
                console.log("id del Proveedor:");
                String id_proveedor_str = scanner.nextLine();

                if(!checker.validar_num(id_proveedor_str)) return;
                id_proveedor = Long.parseLong(id_proveedor_str);

            } else if (tipo.toUpperCase().equals("PRESTAMO")) {
                console.log("id del Usuario:");
                String id_usuario_str = scanner.nextLine();

                if(!checker.validar_num(id_usuario_str)) return;
                id_usuario = Long.parseLong(id_usuario_str);

                console.log("Estado:");
                estado = scanner.nextLine();
            }
        }
        if (tipo.toUpperCase().equals("PRESTAMO") || tipo.toUpperCase().equals("DEVOLUCION")) {
            console.log("¿Agregar o Editar?:");

            String eleccion = scanner.nextLine();

            if (eleccion.toUpperCase().equals("EDITAR")) prestamoRepository.alter(id_recurso, estado);
        }
        
        //pasar a DB_local con addMovimiento(tipo, id_recurso, cantidad, fecha, hora, y los opcionales: id_proveedor, id_usuario, estado);
    }

    @Override
    public void actualizarStock(Salida salida) {
        console.log_alerta(salida);
    }
    
}
