package cl.ucn.app.controller;

import cl.ucn.app.model.*;
import cl.ucn.app.service.*;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BibliotecaController {

    private final BibliotecaService bibliotecaService;
    private final LectorService lectorService;

    public BibliotecaController(BibliotecaService bibliotecaService, LectorService lectorService) {
        this.bibliotecaService = bibliotecaService;
        this.lectorService = lectorService;
    }

    private void exigirSesion(Context ctx) {
        if (ctx.sessionAttribute("usuarioNombre") == null) {
            throw new AccesoDenegadoException(401, "Debe iniciar sesión para acceder al módulo de Biblioteca.");
        }
    }

    private void exigirRolBibliotecario(Context ctx) {
        exigirSesion(ctx);
        if (!esBibliotecario(ctx)) {
            throw new AccesoDenegadoException(403, "Acceso denegado: operación exclusiva para Bibliotecario.");
        }
    }

    private boolean esBibliotecario(Context ctx) {
        String rol = ctx.sessionAttribute("usuarioRol");
        return "ADMIN".equalsIgnoreCase(rol);
    }

    public void showIndex(Context ctx) {
        if (ctx.sessionAttribute("usuarioNombre") == null) {
            ctx.status(401);
            ctx.redirect("/login");
            return;
        }

        String q = ctx.queryParam("q");
        List<Libro> libros = bibliotecaService.buscarLibros(q);
        Map<String, Object> model = construirModeloBase(ctx, libros, q);
        cargarDatosLectorEnModelo(ctx, model);

        ctx.render("biblioteca/index.jte", model);
    }

    private Map<String, Object> construirModeloBase(Context ctx, List<Libro> libros, String q) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Módulo de Biblioteca - SIGU-UCN");
        model.put("usuarioNombre", ctx.sessionAttribute("usuarioNombre"));
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
        model.put("esBibliotecario", esBibliotecario(ctx));
        model.put("libros", libros);
        model.put("busqueda", q != null ? q : "");
        return model;
    }

    private void cargarDatosLectorEnModelo(Context ctx, Map<String, Object> model) {
        String correo = ctx.sessionAttribute("usuarioCorreo");
        if (!esBibliotecario(ctx) && correo != null) {
            Optional<Lector> lectorOpt = lectorService.buscarPorCorreo(correo);
            lectorOpt.ifPresent(lector -> {
                model.put("lector", lector);
                model.put("prestamosActivos", bibliotecaService.consultarPrestamosActivos(lector.getId()));
                model.put("multasPendientes", bibliotecaService.consultarMultasPendientes(lector.getId()));
            });
        }
    }

    public void solicitarPrestamo(Context ctx) {
        exigirSesion(ctx);

        String correo = ctx.sessionAttribute("usuarioCorreo");
        Lector lector = lectorService.buscarPorCorreo(correo)
                .orElseThrow(() -> new DatosInvalidosException("usuario", "El usuario actual no está registrado como lector."));

        Long ejemplarId = parsearLongObligatorio(ctx.formParam("ejemplarId"), "ejemplarId");
        bibliotecaService.solicitarPrestamo(lector.getId(), ejemplarId, LocalDate.now());
        ctx.redirect("/biblioteca?success=prestamo");
    }

    public void registrarDevolucion(Context ctx) {
        exigirRolBibliotecario(ctx);

        String codigoInventario = ctx.formParam("codigoInventario");
        if (codigoInventario == null || codigoInventario.isBlank()) {
            throw new DatosInvalidosException("codigoInventario", "Debe ingresar el código de inventario.");
        }
        boolean danado = "true".equalsIgnoreCase(ctx.formParam("danado"));
        String correo = ctx.sessionAttribute("usuarioCorreo");
        Long bibliotecarioId = bibliotecaService.buscarBibliotecarioPorCorreo(correo)
                .map(Bibliotecario::getId)
                .orElse(null);

        bibliotecaService.registrarDevolucion(codigoInventario.trim(), LocalDate.now(), danado, bibliotecarioId);
        ctx.redirect("/biblioteca?success=devolucion");
    }

    public void registrarPagoMulta(Context ctx) {
        exigirRolBibliotecario(ctx);

        Long multaId = parsearLongObligatorio(ctx.formParam("multaId"), "multaId");
        bibliotecaService.registrarPagoMulta(multaId, LocalDate.now());
        ctx.redirect("/biblioteca?success=pago");
    }

    private Long parsearLongObligatorio(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException(nombreCampo, "El campo " + nombreCampo + " es obligatorio.");
        }
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException(nombreCampo, "El campo " + nombreCampo + " debe ser numérico.");
        }
    }
}
