package cl.ucn.app.controller;

import cl.ucn.app.model.Prestamo;
import cl.ucn.app.service.PrestamoService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * CU03 - Solicitar préstamo de equipo y CU04 - Registrar devolución (Grupo 5).
 */
public class PrestamoController extends InventarioControllerBase {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    /**
     * GET /inventario/prestamos
     */
    public void formularioPrestamo(Context ctx) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("equipos", prestamoService.equiposDisponibles());
        datos.put("misPrestamos", prestamoService.prestamosDeUsuario(usuarioId(ctx)));
        render(ctx, "prestamos.jte", "Solicitar préstamo", datos);
    }

    /**
     * POST /inventario/prestamos
     */
    public void solicitarPrestamo(Context ctx) {
        ejecutarYRedirigir(ctx, "/inventario/prestamos", () -> {
            Prestamo prestamo = prestamoService.solicitarPrestamo(
                    longOpcional(ctx.formParam("equipoId")), usuarioId(ctx));
            return "Préstamo registrado: " + prestamo.getEquipo().getCodigo()
                    + " - " + prestamo.getEquipo().getNombre() + ".";
        });
    }

    /**
     * GET /inventario/devoluciones
     */
    public void listaDevoluciones(Context ctx) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("prestamos", prestamoService.prestamosActivos());
        render(ctx, "devoluciones.jte", "Registrar devolución", datos);
    }

    /**
     * POST /inventario/devoluciones
     */
    public void registrarDevolucion(Context ctx) {
        ejecutarYRedirigir(ctx, "/inventario/devoluciones", () -> {
            Prestamo prestamo = prestamoService.registrarDevolucion(
                    longOpcional(ctx.formParam("prestamoId")), usuarioId(ctx));
            return "Devolución registrada: " + prestamo.getEquipo().getCodigo()
                    + " - " + prestamo.getEquipo().getNombre() + " vuelve a estar disponible.";
        });
    }
}
