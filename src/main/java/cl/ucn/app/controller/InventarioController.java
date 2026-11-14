package cl.ucn.app.controller;

import cl.ucn.app.dto.NuevoProductoDTO;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.Producto;
import cl.ucn.app.service.InventarioService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CU01 - Registrar productos e insumos (Grupo 5).
 */
public class InventarioController extends InventarioControllerBase {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    /**
     * GET /inventario/productos/nuevo
     */
    public void formularioProducto(Context ctx) {
        mostrarFormulario(ctx, Map.of("tipo", "EQUIPO"), null);
    }

    /**
     * POST /inventario/productos/nuevo
     */
    public void registrarProducto(Context ctx) {
        Map<String, String> valores = new HashMap<>();
        for (String campo : List.of("tipo", "codigo", "nombre", "descripcion",
                "categoriaId", "proveedorId", "stockMinimo")) {
            String valor = ctx.formParam(campo);
            valores.put(campo, valor == null ? "" : valor);
        }

        try {
            boolean esInsumo = "INSUMO".equalsIgnoreCase(valores.get("tipo"));
            NuevoProductoDTO datos = new NuevoProductoDTO(
                    valores.get("tipo"),
                    valores.get("codigo"),
                    valores.get("nombre"),
                    valores.get("descripcion"),
                    longOpcional(valores.get("categoriaId")),
                    longOpcional(valores.get("proveedorId")),
                    esInsumo ? enteroOpcional(valores.get("stockMinimo")) : null);

            Producto producto = inventarioService.registrarProducto(datos);
            ctx.sessionAttribute(FLASH_OK, "Producto " + producto.getCodigo() + " - "
                    + producto.getNombre() + " registrado correctamente."
                    + (esInsumo ? " Recuerde registrar su stock inicial en Gestionar stock." : ""));
            ctx.redirect("/inventario");
        } catch (ReglaNegocioException e) {
            ctx.status(400);
            mostrarFormulario(ctx, valores, e.getMessage());
        } catch (RuntimeException e) {
            registrarErrorInesperado(ctx, e);
            ctx.status(500);
            mostrarFormulario(ctx, valores, mensajeErrorInesperado());
        }
    }

    private void mostrarFormulario(Context ctx, Map<String, String> valores, String errorFormulario) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("categorias", inventarioService.categorias());
        datos.put("proveedores", inventarioService.proveedores());
        datos.put("valores", valores);
        datos.put("errorFormulario", errorFormulario);
        render(ctx, "producto-form.jte", "Registrar producto", datos);
    }
}