package cl.ucn.app.controller;

import cl.ucn.app.exception.ReglaNegocioException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Comportamiento común de los controladores del módulo de inventario (Grupo 5):
 * renderizado con los datos de sesión, mensajes flash, lectura segura de
 * parámetros numéricos y manejo controlado de errores.
 */
public abstract class InventarioControllerBase {

    static final String FLASH_OK = "inv.flashOk";
    static final String FLASH_ERROR = "inv.flashError";
    private static final String ERROR_INESPERADO = "Ocurrió un error inesperado. Intente nuevamente.";

    private static final Logger LOG = LoggerFactory.getLogger(InventarioControllerBase.class);

    /**
     * Renderiza una vista de jte/inventario/ agregando título, usuario, rol y mensajes.
     */
    protected void render(Context ctx, String plantilla, String titulo, Map<String, Object> datos) {
        Map<String, Object> modelo = new HashMap<>(datos);
        modelo.put("title", titulo + " - Inventario");
        modelo.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        modelo.put("rol", rol(ctx));
        modelo.put("flashOk", ctx.consumeSessionAttribute(FLASH_OK));
        modelo.put("flashError", ctx.consumeSessionAttribute(FLASH_ERROR));
        ctx.render("inventario/" + plantilla, modelo);
    }

    /**
     * Patrón POST-redirect-GET: ejecuta la acción, guarda el mensaje de éxito o
     * de error en la sesión y redirige. Los errores inesperados se registran en
     * el log sin exponer detalles internos al usuario.
     */
    protected void ejecutarYRedirigir(Context ctx, String destino, Supplier<String> accion) {
        try {
            ctx.sessionAttribute(FLASH_OK, accion.get());
        } catch (ReglaNegocioException e) {
            ctx.sessionAttribute(FLASH_ERROR, e.getMessage());
        } catch (RuntimeException e) {
            LOG.error("Error inesperado en {}", ctx.path(), e);
            ctx.sessionAttribute(FLASH_ERROR, ERROR_INESPERADO);
        }
        ctx.redirect(destino);
    }

    protected void error(Context ctx, String mensaje) {
        ctx.sessionAttribute(FLASH_ERROR, mensaje);
    }

    protected void registrarErrorInesperado(Context ctx, RuntimeException e) {
        LOG.error("Error inesperado en {}", ctx.path(), e);
    }

    protected static String mensajeErrorInesperado() {
        return ERROR_INESPERADO;
    }

    protected Long usuarioId(Context ctx) {
        return ctx.sessionAttribute("usuarioId");
    }

    protected String rol(Context ctx) {
        return ctx.sessionAttribute("usuarioRol");
    }

    protected Long idDeRuta(Context ctx) {
        Long id = longOpcional(ctx.pathParam("id"));
        if (id == null) {
            throw new ReglaNegocioException("Identificador no válido.");
        }
        return id;
    }

    /**
     * Convierte un parámetro de texto a Long; vacío o null se interpreta como "sin valor".
     */
    protected static Long longOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("El valor \"" + valor + "\" no es un número válido.");
        }
    }

    protected static Integer enteroOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException("El valor \"" + valor + "\" no es un número entero válido.");
        }
    }
}
