package cl.ucn.app.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import cl.ucn.app.model.MovimientoInventario;
import cl.ucn.app.model.Proveedor;
import cl.ucn.app.model.Recurso;
import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.service.ConsoleService;
import cl.ucn.app.service.MovimientoFactoryService;
import cl.ucn.app.service.RecursoFactoryService;


/**
 * En proceso de descartarse (...)
 */
public class DB_local {
    public static ArrayList<Recurso> Recursos = new ArrayList<Recurso>();
    public static ArrayList<MovimientoInventario> Movimientos = new ArrayList<MovimientoInventario>();
    public static ArrayList<Usuario> Usuarios = new ArrayList<Usuario>();
    public static ArrayList<Proveedor> Proveedores = new ArrayList<Proveedor>();

    private static RecursoRepository recursoRepository;
    private static ProveedorRepository proveedorRepository;
    private static UsuarioRepository usuarioRepository;
    private static RecursoFactoryService recFactory;
    private static MovimientoFactoryService movFactory;
    private static MovimientoInventarioRepository movimientoRepository;
    private static ConsoleService console;

    public DB_local() {
        recursoRepository = new RecursoRepository();
        proveedorRepository = new ProveedorRepository();
        usuarioRepository = new UsuarioRepository();
        recFactory = new RecursoFactoryService();
        movFactory = new MovimientoFactoryService();
        movimientoRepository = new MovimientoInventarioRepository();
        console = new ConsoleService();
    }

    public static ArrayList<Recurso> getRecursos() {
        return Recursos;
    }

    public static ArrayList<Proveedor> getProveedores() {
        return Proveedores;
    }

    public static ArrayList<MovimientoInventario> getMovimientos() {
        return Movimientos;
    }

    public static ArrayList<Usuario> getUsuarios() {
        return Usuarios;
    }

    public static void addRecurso(String nombre, int stock, String tipo) {
        Recurso recurso = null;
        if (tipo.equals("INSUMO")) recurso = recFactory.crearInsumo(nombre, stock);
        if (tipo.equals("EQUIPO")) recurso = recFactory.crearEquipo(nombre, stock);

        Recursos.add(recurso);
        recursoRepository.save(recurso);
    }

    public static void addProveedor(String nombre, String correo, String telefono) {
        Proveedor proveedor = new Proveedor(nombre, correo, telefono);

        Proveedores.add(proveedor);
        proveedorRepository.save(proveedor);
    }

    public static void addUsuario(String nombre, String correo, String password, Boolean activo, Rol rol) {
        Usuario usuario = new Usuario(nombre, correo, password, activo, rol);

        Usuarios.add(usuario);
        usuarioRepository.save(usuario);
    }

}
